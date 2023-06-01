package io.github.pricescrawler.content.repository.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogDataRepository extends MongoRepository<CatalogDao, String> {
    /**
     * Finds all catalog data entities that contain the specified locale.
     *
     * @param locale the locale to search for
     * @return a list of catalog data entities containing the specified locale
     */
    List<CatalogDao> findAllByLocalesContains(String locale);

    /**
     * Finds all catalog data entities that contain the specified locale and category.
     *
     * @param locale   the locale to search for
     * @param category the category to search for
     * @return a list of catalog data entities containing the specified locale and category
     */
    List<CatalogDao> findAllByLocalesContainsAndCategoriesContains(String locale, String category);
}
