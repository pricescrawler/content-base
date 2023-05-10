package io.github.pricescrawler.content.service.catalog;

import io.github.pricescrawler.content.common.dto.catalog.CatalogDto;
import io.github.pricescrawler.content.common.dto.catalog.CategoryDto;
import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataRepository;
import io.github.pricescrawler.content.repository.catalog.CategoryDataRepository;
import io.github.pricescrawler.content.repository.catalog.LocaleDataRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class SimpleCatalogService implements CatalogService {
    private final LocaleDataRepository localeDataRepository;
    private final CatalogDataRepository catalogDataRepository;
    private final CategoryDataRepository categoryDataRepository;

    public SimpleCatalogService(CatalogDataRepository catalogDataRepository, LocaleDataRepository localeDataRepository,
                                CategoryDataRepository categoryDataRepository) {
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

    private List<CategoryDto> getCategoriesAndCatalogs(LocaleDto locale) {
        var categoryNameSet = new HashSet<String>();
        catalogDataRepository.findAllByLocalesContains(locale.getId())
                .stream()
                .flatMap(catalog -> catalog.getCategories().stream())
                .forEach(categoryNameSet::add);

        var categories = new ArrayList<CategoryDto>();
        for (var categoryName : categoryNameSet) {
            var catalogs = catalogDataRepository
                    .findAllByLocalesContainsAndCategoriesContains(locale.getId(), categoryName)
                    .stream().map(CatalogDto::new).toList();
            var optionalCategory = categoryDataRepository.findById(categoryName).map(CategoryDto::new);

            optionalCategory.ifPresent(category -> {
                category.setCatalogs(catalogs);
                categories.add(category);
            });
        }
        return categories;
    }
}
