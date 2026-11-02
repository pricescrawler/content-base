package io.github.pricescrawler.content.repository;

import io.github.pricescrawler.content.common.dao.product.PriceDao;
import io.github.pricescrawler.content.util.BaseSpringBootTest;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriceStorageTest extends BaseSpringBootTest {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Test
    void pricesAreStoredAsDecimalsWithoutCopiesOfTheProduct() {
        var product = getProductDao();
        product.setId("local.demo.storage");
        product.setPrices(List.of(PriceDao.builder()
                .regularPrice("1,20 €")
                .regularPriceValue(new BigDecimal("1.20"))
                .date("2026-10-01T10:00:00Z")
                .build()));
        productHistoryDataRepository.save(product).block();

        var raw = mongoTemplate.findOne(Query.query(Criteria.where("_id").is("local.demo.storage")), Document.class,
                "product-history").block();
        assertNotNull(raw);
        var price = raw.getList("prices", Document.class).getFirst();

        assertInstanceOf(Decimal128.class, price.get("regularPriceValue"));
        assertFalse(price.containsKey("name"));
        assertFalse(price.containsKey("data"));

        var read = productHistoryDataRepository.findById("local.demo.storage").block();
        assertNotNull(read);
        assertEquals(0, new BigDecimal("1.20").compareTo(read.getPrices().getFirst().getRegularPriceValue()));
    }

    @Test
    void historyWithADateRange() {
        webTestClient.get().uri("/api/v1/products/history/local/demo/1?startDate=2020-01-01T00:00:00Z&endDate=2100-01-01T00:00:00Z")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.prices.length()").isEqualTo(1);
    }
}
