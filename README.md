# Prices Crawler - Content Base

Reactive Spring Boot framework (Maven library) for building product price-crawling
APIs: MongoDB persistence, caching, price history, product lists, incident detection
and the REST API. Deployable implementations (e.g.
[content-api-example](https://github.com/prices-crawler/content-api-example)) extend
it with one product service per catalog.

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
├── prices-crawler-content-mcp/          # MCP server (read-only tools for AI agents)
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
| `ACTIVE_PROFILE` | Spring profile (`dev`, `prod`, …; default `default`) |
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
| `prices.crawler.controller.mcp.enabled`            | `false` | Enable the MCP server at `/mcp`                                      |
| `prices.crawler.controller.mcp.authenticated`      | `false` | Require basic auth on `/mcp` (reference application)                 |

## 🤖 MCP Server

`prices-crawler-content-mcp` exposes the price data to AI agents (Claude, IDEs, …) through
the [Model Context Protocol](https://modelcontextprotocol.io), using Spring AI's stateless
Streamable HTTP transport. Implementations opt in by adding the module as a dependency and
setting `prices.crawler.controller.mcp.enabled=true`.

| Tool                       | Purpose                                                    |
|----------------------------|------------------------------------------------------------|
| `list_catalogs`            | Active catalogs, their categories and stores               |
| `search_products`          | Live text search in up to 10 catalogs (max 50 per catalog) |
| `get_price_history`        | Stored price history of one product                        |
| `find_products_by_barcode` | The same EAN/UPC across catalogs, to compare prices        |
| `get_shared_list`          | Products of a list shared from the web app                 |

All tools are read-only: ingest, parser and list storage are not exposed. Any
`spring.ai.mcp.server.*` property overrides the module defaults (name `prices-crawler`,
stateless async server, 60 s request timeout).

Connect from Claude Code:

```bash
claude mcp add --transport http prices-crawler http://localhost:8080/mcp
```

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
