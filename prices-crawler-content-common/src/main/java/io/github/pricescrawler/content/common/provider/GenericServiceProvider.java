package io.github.pricescrawler.content.common.provider;

import io.github.pricescrawler.content.common.util.IdUtils;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.ApplicationContext;

public class GenericServiceProvider<T> {
    private final Class<T> classType;
    private final ApplicationContext appContext;

    public GenericServiceProvider(ApplicationContext appContext, Class<T> classType) {
        this.appContext = appContext;
        this.classType = classType;
    }

    public T getServiceFromCatalog(String catalogAlias) {
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(appContext.getAutowireCapableBeanFactory(), classType,
                IdUtils.extractCatalogFromComposedKey(catalogAlias));
    }
}
