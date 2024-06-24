package io.crossplane.compositefunctions.starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {"io.crossplane.compositefunctions.starter.conversion"})
public class CrossplaneServiceConfiguration {
}
