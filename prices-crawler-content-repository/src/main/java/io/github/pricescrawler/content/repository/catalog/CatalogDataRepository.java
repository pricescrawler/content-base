package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CatalogDataRepository extends ReactiveMongoRepository<CatalogDao, String> {
    /**
     * Finds all catalog data entities that contain the specified locale.
     *
     * @param locale the locale to search for
     * @return a list of catalog data entities containing the specified locale
     */
    Flux<CatalogDao> findAllByLocalesContains(String locale);

    /**
     * Finds all catalog data entities that contain the specified locale and category.
     *
     * @param locale   the locale to search for
     * @param category the category to search for
     * @return a list of catalog data entities containing the specified locale and category
     */
    Flux<CatalogDao> findAllByLocalesContainsAndCategoriesContains(String locale, String category);
}
