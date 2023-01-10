package io.github.scafer.prices.crawler.content.service.catalog;

import io.github.scafer.prices.crawler.content.common.dto.catalog.CatalogDto;
import io.github.scafer.prices.crawler.content.common.dto.catalog.CategoryDto;
import io.github.scafer.prices.crawler.content.common.dto.catalog.LocaleDto;
import io.github.scafer.prices.crawler.content.repository.catalog.CatalogDataRepository;
import io.github.scafer.prices.crawler.content.repository.catalog.CategoryDataRepository;
import io.github.scafer.prices.crawler.content.repository.catalog.LocaleDataRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleCatalogService implements CatalogService {
    private final CatalogDataRepository catalogDataRepository;
    private final LocaleDataRepository localeDataRepository;
    private final CategoryDataRepository categoryDataRepository;

    public SimpleCatalogService(CatalogDataRepository catalogDataRepository, LocaleDataRepository localeDataRepository, CategoryDataRepository categoryDataRepository) {
        this.catalogDataRepository = catalogDataRepository;
        this.localeDataRepository = localeDataRepository;
        this.categoryDataRepository = categoryDataRepository;
    }

    @Override
    public List<LocaleDto> searchLocales() {
        var locales = localeDataRepository.findAll().stream().map(LocaleDto::new).toList();

        for (var locale : locales) {
            locale.setCategories(getCategoriesAndCatalogs(locale));
        }

        return locales;
    }

    @Override
    public Optional<LocaleDto> searchLocaleById(String id) {
        var optionalLocale = localeDataRepository.findById(id).map(LocaleDto::new);

        if (optionalLocale.isPresent()) {
            var localeDto = optionalLocale.get();
            localeDto.setCategories(getCategoriesAndCatalogs(localeDto));
        }

        return optionalLocale;
    }

    private ArrayList<CategoryDto> getCategoriesAndCatalogs(LocaleDto locale) {
        var categories = new ArrayList<CategoryDto>();
        var categoryNameSet = new HashSet<String>();

        for (var catalog : catalogDataRepository.findAllByLocalesContains(locale.getId())) {
            categoryNameSet.addAll(catalog.getCategories());
        }

        for (var categoryName : categoryNameSet) {
            var catalogs = catalogDataRepository.findAllByLocalesContainsAndCategoriesContains(locale.getId(), categoryName).stream().map(CatalogDto::new).toList();
            var optionalCategory = categoryDataRepository.findById(categoryName).map(CategoryDto::new);

            if (optionalCategory.isPresent()) {
                var category = optionalCategory.get();
                category.setCatalogs(catalogs);
                categories.add(category);
            }
        }
        return categories;
    }
}
