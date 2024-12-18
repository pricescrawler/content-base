package io.github.pricescrawler.content.service.product.provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceProviderTest {
    @Mock
    private ApplicationContext mockAppContext;
    @Mock
    private AutowireCapableBeanFactory mockBeanFactory;

    @Test
    void testServiceRetrievalFromCatalog_notFound() {
        when(mockAppContext.getAutowireCapableBeanFactory()).thenReturn(mockBeanFactory);

        var productServiceProvider = new ProductServiceProvider(mockAppContext);

        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () ->
                productServiceProvider.getServiceFromCatalog("local.catalog"));
    }
}
