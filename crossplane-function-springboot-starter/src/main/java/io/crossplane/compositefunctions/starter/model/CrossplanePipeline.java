package io.crossplane.compositefunctions.starter.model;

import io.crossplane.apiextensions.v1.compositionspec.pipeline.Requirements;

public record CrossplanePipeline(String name, String functionReference, Requirements requirements) {

    public CrossplanePipeline(String name) {
        this(name, name, null);
    }

    public CrossplanePipeline(String name, String functionReference) {
        this(name, functionReference, null);
    }

    public CrossplanePipeline(String name, Requirements requirements) {
        this(name, name, requirements);
    }
}
