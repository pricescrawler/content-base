# Prices Crawler - Content Base

## 💻 Description

The main goal of this project is to provide a framework to search, store and retrieve product data, enabling price
comparison and analysis.

**Open API URL:** http://localhost:8080/swagger-ui.html

## 📁 Requirements

| # | name    | Value   |
|---|---------|---------|
| 1 | `Java`  | `17`    |
| 2 | `Maven` | `3.8.6` |

## 🕹️ Getting Started

### Environment Variables

| # | Name           | Type   | Description          | Default |
|---|----------------|--------|----------------------|---------|
| 1 | ACTIVE_PROFILE | String | Spring profile name  | -       |
| 2 | PORT           | int    | Service port         | 8080    |
| 3 | DATABASE_URL   | String | Database path url    | -       |
| 4 | DATABASE_NAME  | String | Database schema name | -       |

### Spring Environment Properties

| #  | Name                                              | Type    | Description            | Default     |
|----|---------------------------------------------------|---------|------------------------|-------------|
| 1  | spring.data.mongodb.uri                           | String  | Mongodb URI            | -           |
| 2  | spring.data.mongodb.database                      | String  | Mongodb database name  | -           |
| 3  | prices.crawler.cache.enabled                      | Boolean | Cache service          | true        |
| 4  | prices.crawler.history.enabled                    | Boolean | Prices history service | true        |
| 5  | prices.crawler.product-incident.enabled           | Boolean | Product incident check | true        |
| 6  | prices.crawler.controller.catalog.enabled         | Boolean | Catalog controller     | false       |
| 7  | prices.crawler.controller.product.list.enabled    | Boolean | Product controller     | false       |
| 8  | prices.crawler.controller.product.search.enabled  | Boolean | Product controller     | false       |
| 9  | prices.crawler.controller.product.parser.enabled  | Boolean | Product controller     | false       |
| 10 | prices.crawler.controller.product.history.enabled | Boolean | Product controller     | false       |
| 11 | prices.crawler.background.service.cron            | String  | Cron String            | 0 0 0 * * * |

### MongoDB Configurations

- To ensure maximum performance create the following indexes:
    - _Collection:_ products
        - eanUpcList_1
