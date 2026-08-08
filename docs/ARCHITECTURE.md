# Architecture

Multi-module Maven library for building product price-crawling APIs. Implementations
depend on `prices-crawler-content-controller` and provide one product service per
catalog; everything else (REST API, caching, history, persistence) comes from here.

## Modules

| Module                               | Contents                                                                                                                                              |
|--------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `prices-crawler-content-common`      | DTOs (`ProductDto`, `SearchProductsDto`, …), DAOs (`ProductDao`, `CatalogDao`, …), utilities (`RestClient`, `IdUtils`, `DateTimeUtils`, `PriceUtils`) |
| `prices-crawler-content-repository`  | Reactive MongoDB repositories + data services (products, history, incidents, catalogs, cache, lists)                                                  |
| `prices-crawler-content-service`     | `BaseProductService` (extension point), cache services, product lists, `ProductServiceProvider`, `BackgroundService`, `DemoProductService`            |
| `prices-crawler-content-controller`  | REST controllers (see [API.md](./API.md)), each behind a `@ConditionalOnProperty` toggle                                                              |
| `prices-crawler-content-application` | Reference Spring Boot application (WebFlux security config + entry point)                                                                             |

## Extension contract

An implementation registers one Spring bean per catalog, qualified by
`<locale>.<catalog>`:

```java

@Service
@Qualifier("local.example")
public class ExampleProductService extends BaseProductService {
    // searchItemLogic(FilterProductByQueryDto)           -> search by query
    // searchItemByProductUrlLogic(FilterProductByUrlDto) -> fetch one product by URL
    // updateItemLogic(ProductListItemDto)                -> refresh a product-list item
}
```

`ProductServiceProvider` resolves the bean from the composed catalog key of each
request. `BaseProductService` wraps the three methods with:

1. **Catalog gating** — locale/catalog/store `active` flags short-circuit to an empty
   result. The locale/catalog snapshot is loaded in the constructor and refreshed
   every `prices.crawler.catalog.data.refresh-seconds` (default 300; `0` = startup
   snapshot only). Values captured by subclasses in their constructors (e.g.
   `getBaseUrl()`) are **not** refreshed.
2. **Cache** — MongoDB-backed (`mongoDbProductCacheService`) or in-memory
   (`inMemoryProductCacheService`) result cache per (locale, catalog, query).
3. **Persistence** — aggregated history (per search term) and/or individual product
   documents, saved asynchronously after a live search.
4. **Price enrichment** — `PriceUtils.enrichPrices` derives numeric fields
   (`regularPriceValue`, `campaignPriceValue`, `currency`, `pricePerQuantityValue`,
   `pricePerQuantityUnit`) from the scraped display strings; implementations may set
   them directly to skip parsing.
5. **Metrics** — counter `prices.crawler.product.search` tagged
   `locale`/`catalog`/`type`(query|url|update)/`outcome`(cached|success|empty|error).
   `empty`/`error` spikes are the broken-parser signal.
6. **Error isolation** — fetch/parse failures are logged and converted into empty
   results; clients always get a well-formed payload.

## Data model (MongoDB)

| Collection / DAO                                                           | Content                                                                                                                                                           |
|----------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `locale`, `catalog`, `category` (`LocaleDao`, `CatalogDao`, `CategoryDao`) | Catalog topology; per-entity flags `isActive`, `isCacheEnabled`, `isHistoryEnabled`; `CatalogDao.data` holds implementation extras (base URLs, cookies, API keys) |
| products (`ProductDao`)                                                    | One document per product with embedded `PriceDao` history entries and `eanUpcList`                                                                                |
| history (`ProductHistoryDao`)                                              | Aggregated per-product history built from search results, with `searchTerms`                                                                                      |
| cache (`ProductCacheDao`)                                                  | Cached search results with timestamps                                                                                                                             |
| incidents (`ProductIncidentDao`)                                           | Detected anomalies awaiting triage (toggle `prices.crawler.product-incident.enabled`)                                                                             |
| lists (`ProductListDao`)                                                   | Shared product lists with expiration                                                                                                                              |

Identifiers: product id = `IdUtils.parse(locale, catalog, reference)` →
`<locale>.<catalog>.<reference>`; composed catalog key = `<locale>.<catalog>`.

## Background maintenance

`BackgroundService` (cron `prices.crawler.background.service.cron`, default midnight;
gated by `...cron.enabled`) deletes outdated product lists and outdated cache entries
(both cache implementations).
