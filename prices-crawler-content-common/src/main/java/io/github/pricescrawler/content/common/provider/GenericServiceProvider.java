package io.github.pricescrawler.content.common.provider;

import io.github.pricescrawler.content.common.util.IdUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

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

    /**
     * Same as {@link #getServiceFromCatalog(String)}, but returns an empty
     * {@link Optional} instead of throwing when no service is registered for the catalog.
     */
    public Optional<T> findServiceFromCatalog(String catalogAlias) {
        try {
            return Optional.of(getServiceFromCatalog(catalogAlias));
        } catch (BeansException ex) {
            return Optional.empty();
        }
    }
}
