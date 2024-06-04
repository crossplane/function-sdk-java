package io.crossplane.compositefunctions;

import io.crossplane.compositefunctions.protobuf.ResourceSelector;
import io.crossplane.compositefunctions.protobuf.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CrossplaneFunctionResponse(Map<String, Object> desiredResources,
                                         Map<String, ResourceSelector> resourceSelectors,
                                         List<Result> results) {


    public CrossplaneFunctionResponse() {
        this(new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }
}
