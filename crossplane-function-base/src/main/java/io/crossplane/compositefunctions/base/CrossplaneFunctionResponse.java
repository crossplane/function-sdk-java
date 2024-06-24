package io.crossplane.compositefunctions.base;

import io.crossplane.compositefunctions.protobuf.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holder for the response to Crossplane
 * @param desiredResources Map of the desired resources
 * @param results List of the results
 */
public record CrossplaneFunctionResponse(Map<String, Object> desiredResources,
                                         List<Result> results) {

    // @param resourceSelectors Map of the resource selectors for any extra resources
    // Map<String, ResourceSelector> resourceSelectors,


    /**
     * Create an empty response with all fields initiated
      */
    public CrossplaneFunctionResponse() {
        this(new HashMap<>(), new ArrayList<>());
    }
}
