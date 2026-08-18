# Prices Crawler - Content Base

Reactive Spring Boot framework (Maven library) for building product price-crawling
APIs: MongoDB persistence, caching, price history, product lists, incident detection
and the REST API. Deployable implementations (e.g.
[content-api-example](https://github.com/prices-crawler/content-api-example)) extend
it with one product service per catalog.

**Version:** 0.5.1-SNAPSHOT

## 📁 Requirements

| Component   | Version |
|-------------|---------|
| Java        | 25+     |
| Maven       | 3.9.6+  |
| MongoDB     | 4.0+    |
| Spring Boot | 4.1.0   |

## 📂 Modules

```
content-base/
├── prices-crawler-content-application/  # reference Spring Boot app
├── prices-crawler-content-controller/   # REST controllers
├── prices-crawler-content-service/      # BaseProductService, cache, lists, scheduler
├── prices-crawler-content-repository/   # reactive MongoDB repositories
└── prices-crawler-content-common/       # DTOs, DAOs, utilities
```

## 🚀 Getting Started

```bash
mvn clean package
export DATABASE_URL=mongodb://localhost:27017
export DATABASE_NAME=prices_crawler
export ACTIVE_PROFILE=dev
java -jar prices-crawler-content-application/target/*.jar
```

API: `http://localhost:8080` · Swagger UI: `http://localhost:8080/swagger-ui.html`

### Environment Variables

| Variable         | Description                       |
|------------------|-----------------------------------|
| `ACTIVE_PROFILE` | Spring profile (`dev`, `prod`, …) |
| `PORT`           | HTTP server port (default `8080`) |
| `DATABASE_URL`   | MongoDB connection URI            |
| `DATABASE_NAME`  | MongoDB database name             |

### Key feature toggles

| Property                                           | Default | Purpose                                                              |
|----------------------------------------------------|---------|----------------------------------------------------------------------|
| `prices.crawler.cache.enabled`                     | `true`  | Result caching                                                       |
| `prices.crawler.history.enabled`                   | `true`  | Price history tracking                                               |
| `prices.crawler.catalog.data.refresh-seconds`      | `300`   | Locale/catalog toggle refresh interval (`0` = startup snapshot only) |
| `prices.crawler.controller.product.search.enabled` | `false` | Enable the search endpoint                                           |
| `prices.crawler.background.service.cron.enabled`   | `false` | Enable the background cleanup job                                    |

## 📚 Documentation

[docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md) — modules, extension contract, data
model, background maintenance.

## 🚀 Release Process (SemVer)

**GitHub Actions** → **Release (SemVer)** → **Run workflow** (`main` branch only),
choosing `major`/`minor`/`patch`. This computes the next version from `pom.xml`,
updates all Maven modules, tags and publishes a GitHub Release, then bumps to the next
snapshot.

## 🤝 Contributing

Issues: [content-base/issues](https://github.com/pricescrawler/content-base/issues).

## 📄 License

[MIT License](./LICENSE).
