package io.github.pricescrawler.content.service.product.provider;

import io.github.pricescrawler.content.service.product.ProductService;
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
public class ProductServiceProviderTest {
    @Mock
    private ApplicationContext mockAppContext;
    @Mock
    private AutowireCapableBeanFactory mockBeanFactory;
    @Mock
    private ProductService mockProductService;

    @Test
    public void testServiceRetrievalFromCatalog_notFound() {
        when(mockAppContext.getAutowireCapableBeanFactory()).thenReturn(mockBeanFactory);

        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> {
            new ProductServiceProvider(mockAppContext).getServiceFromCatalog("local.catalog");
        });
    }
}
