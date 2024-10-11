package io.crossplane.compositefunctions.starter.config;

import io.crossplane.compositefunctions.starter.conversion.CrossplaneExtraResourcesService;
import io.crossplane.compositefunctions.starter.conversion.CrossplaneObservableService;
import io.crossplane.compositefunctions.starter.conversion.CrossplaneResourceService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguration for the crossplane services.
 */
@AutoConfiguration
public class CrossplaneServiceConfiguration {


    /**
     * Set up services for working with extra resources
     * @return the crossplaneExtraResourcesService
     * @since 1.15
     */
    @Bean
    public CrossplaneExtraResourcesService crossplaneExtraResourcesService() {
        return new CrossplaneExtraResourcesService();
    }

    /**
     * Set up services for working with observed resources
     * @return the crossplaneObservableService
     * @since 1.14
     */
    @Bean
    public CrossplaneObservableService crossplaneObservableService() {
        return new CrossplaneObservableService();
    }

    /**
     * Set up services for working with default resources
     * @return the crossplaneResourceService
     * @since 1.15
     */
    @Bean
    public CrossplaneResourceService crossplaneResourceService() {
        return new CrossplaneResourceService();
    }

}
