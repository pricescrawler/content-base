# Prices Crawler - Content Base

## 💻 Description

The main goal of this project is to provide a framework to search, store and retrieve product data, enabling price
comparison and analysis.

**Open API URL:** http://localhost:8080/swagger-ui.html

## 📁 Requirements

| # | name    | Value   |
|---|---------|---------|
| 1 | `Java`  | `21`    |
| 2 | `Maven` | `3.9.6` |

## ⚠️ Known Issues

Please report if you find any! 🙂

## 🕹️ Getting Started

### Environment Variables

| # | Name           | Type   | Description          | Default |
|---|----------------|--------|----------------------|---------|
| 1 | ACTIVE_PROFILE | String | Spring profile name  | -       |
| 2 | PORT           | int    | Service port         | 8080    |
| 3 | DATABASE_URL   | String | Database path url    | -       |
| 4 | DATABASE_NAME  | String | Database schema name | -       |

### Spring Environment Properties

| #  | Name                                              | Type    | Description                  | Default     |
|----|---------------------------------------------------|---------|------------------------------|-------------|
| 1  | spring.data.mongodb.uri                           | String  | Mongodb URI                  | -           |
| 2  | spring.data.mongodb.database                      | String  | Mongodb database name        | -           |
| 3  | prices.crawler.cache.enabled                      | Boolean | Cache service                | true        |
| 4  | prices.crawler.history.enabled                    | Boolean | Prices history service       | true        |
| 5  | prices.crawler.history.individual.enabled         | Boolean | Product controller           | true        |
| 6  | prices.crawler.history.aggregated.enabled         | Boolean | Product controller           | true        |
| 7  | prices.crawler.product-incident.enabled           | Boolean | Product incident check       | true        |
| 8  | prices.crawler.controller.catalog.enabled         | Boolean | Catalog controller           | false       |
| 9  | prices.crawler.controller.product.enabled         | Boolean | Product controller           | false       |
| 10 | prices.crawler.controller.product.list.enabled    | Boolean | Product controller           | false       |
| 11 | prices.crawler.controller.product.history.enabled | Boolean | Product controller           | false       |
| 12 | prices.crawler.controller.product.search.enabled  | Boolean | Product controller           | false       |
| 13 | prices.crawler.controller.product.parser.enabled  | Boolean | Product controller           | false       |
| 14 | prices.crawler.background.service.cron            | String  | Cron string                  | 0 0 0 * * * |
| 15 | prices.crawler.product.data.hintsEnabled          | String  | Product hints enabled        | true        |
| 16 | prices.crawler.product.data.searchTermsEnabled    | String  | Product search terms enabled | true        |

### MongoDB Configurations

- To ensure maximum performance create the following indexes:
    - _Collection:_ products
        - eanUpcList_1
