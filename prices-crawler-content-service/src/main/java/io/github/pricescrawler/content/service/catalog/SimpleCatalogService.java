package io.github.pricescrawler.content.service.catalog;

import io.github.pricescrawler.content.common.dto.catalog.CatalogDto;
import io.github.pricescrawler.content.common.dto.catalog.CategoryDto;
import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataRepository;
import io.github.pricescrawler.content.repository.catalog.CategoryDataRepository;
import io.github.pricescrawler.content.repository.catalog.LocaleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SimpleCatalogService implements CatalogService {
    private final LocaleDataRepository localeDataRepository;
    private final CatalogDataRepository catalogDataRepository;
    private final CategoryDataRepository categoryDataRepository;

    @Override
    public Flux<LocaleDto> searchLocales() {
        return localeDataRepository.findAll()
                .map(LocaleDto::new)
                .flatMap(locale -> getCategoriesAndCatalogs(locale)
                        .collectList()
                        .doOnNext(locale::setCategories)
                        .thenReturn(locale));
    }

    @Override
    public Mono<LocaleDto> searchLocaleById(String id) {
        return localeDataRepository.findById(id)
                .map(LocaleDto::new)
                .flatMap(locale -> getCategoriesAndCatalogs(locale)
                        .collectList()
                        .doOnNext(locale::setCategories)
                        .thenReturn(locale));
    }

    private Flux<CategoryDto> getCategoriesAndCatalogs(LocaleDto locale) {
        return catalogDataRepository.findAllByLocalesContains(locale.getId())
                .flatMap(catalog -> Flux.fromIterable(catalog.getCategories()))
                .distinct()
                .flatMap(categoryName -> Mono.zip(
                        catalogDataRepository.findAllByLocalesContainsAndCategoriesContains(locale.getId(), categoryName)
                                .map(CatalogDto::new)
                                .collectList(),
                        categoryDataRepository.findById(categoryName).map(CategoryDto::new)
                )
                .map(tuple -> {
                    var category = tuple.getT2();
                    category.setCatalogs(tuple.getT1());
                    return category;
                }));
    }
}
