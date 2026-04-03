package io.github.pricescrawler.content.service.catalog;

import io.github.pricescrawler.content.common.dao.catalog.CatalogDao;
import io.github.pricescrawler.content.common.dao.catalog.CategoryDao;
import io.github.pricescrawler.content.common.dao.catalog.LocaleDao;
import io.github.pricescrawler.content.common.dto.catalog.CatalogDto;
import io.github.pricescrawler.content.common.dto.catalog.CategoryDto;
import io.github.pricescrawler.content.common.dto.catalog.LocaleDto;
import io.github.pricescrawler.content.repository.catalog.CatalogDataRepository;
import io.github.pricescrawler.content.repository.catalog.CategoryDataRepository;
import io.github.pricescrawler.content.repository.catalog.LocaleDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleCatalogServiceTest {
    @InjectMocks
    private SimpleCatalogService simpleCatalogService;

    @Mock
    private LocaleDataRepository localeDataRepository;

    @Mock
    private CatalogDataRepository catalogDataRepository;

    @Mock
    private CategoryDataRepository categoryDataRepository;

    @Test
    void testSearchLocales() {
        var locale1 = LocaleDao.builder().id("1").build();
        var locale2 = LocaleDao.builder().id("2").build();

        var locales = List.of(new LocaleDto(locale1), new LocaleDto(locale2));
        when(localeDataRepository.findAll()).thenReturn(Flux.just(locale1, locale2));
        when(catalogDataRepository.findAllByLocalesContains(anyString())).thenReturn(Flux.empty());

        var result = simpleCatalogService.searchLocales().collectList().block();

        verify(localeDataRepository, times(1)).findAll();
        assertEquals(locales.size(), result.size());
    }

    @Test
    void testSearchLocaleById_withoutCategories() {
        var id = "1";
        var localeDao = LocaleDao.builder().id(id).build();
        var localeDto = LocaleDto.builder().id(id).isActive(true).categories(List.of()).data(Map.of()).build();

        when(localeDataRepository.findById(id)).thenReturn(Mono.just(localeDao));
        when(catalogDataRepository.findAllByLocalesContains(anyString())).thenReturn(Flux.empty());

        var result = simpleCatalogService.searchLocaleById(id).block();

        verify(localeDataRepository, times(1)).findById(id);
        assertEquals(localeDto, result);
    }

    @Test
    void testSearchLocaleById_withCategories() {
        var localeId = "locale";
        var categoryId = "category";
        var localeDao = LocaleDao.builder().id(localeId).build();
        var catalogDao = CatalogDao.builder().categories(List.of(categoryId)).build();
        var categoryDao = CategoryDao.builder().name(categoryId).build();

        when(localeDataRepository.findById(localeId)).thenReturn(Mono.just(localeDao));
        when(categoryDataRepository.findById(categoryId)).thenReturn(Mono.just(categoryDao));
        when(catalogDataRepository.findAllByLocalesContains(anyString())).thenReturn(Flux.just(catalogDao));
        when(catalogDataRepository.findAllByLocalesContainsAndCategoriesContains(anyString(), anyString()))
                .thenReturn(Flux.just(catalogDao));

        var result = simpleCatalogService.searchLocaleById(localeId).block();

        verify(localeDataRepository, times(1)).findById(localeId);

        var catalogDto = CatalogDto.builder().isActive(true).data(Map.of()).build();
        var categoryDto = CategoryDto.builder().name(categoryId).isActive(true).catalogs(List.of(catalogDto)).data(Map.of()).build();
        var localeDto = LocaleDto.builder().id(localeId).isActive(true).categories(List.of(categoryDto)).data(Map.of()).build();

        assertEquals(localeDto, result);
    }
}
