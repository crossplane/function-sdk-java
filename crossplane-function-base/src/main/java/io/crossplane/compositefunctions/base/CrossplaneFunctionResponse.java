package io.crossplane.compositefunctions.base;

import io.crossplane.compositefunctions.protobuf.v1.Condition;
import io.crossplane.compositefunctions.protobuf.v1.ResourceSelector;
import io.crossplane.compositefunctions.protobuf.v1.Result;
import io.crossplane.compositefunctions.protobuf.v1.SchemaSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holder for the response to Crossplane
 * @param desiredResources Map of the desired resources
 * @param resourceSelectors Map of the resource selectors for any required resources
 * @param schemaSelectors Map of the schema selectors for any required schemas
 * @param results List of the results
 * @param conditions List of the conditions of the function response
 * @param ttl Time to live for the response in positive seconds. This is an alpha feature, and needs to be explicitly enabled in Crossplane.
 */
public record CrossplaneFunctionResponse(Map<String, Object> desiredResources,
                                         Map<String, ResourceSelector> resourceSelectors,
                                         Map<String, SchemaSelector> schemaSelectors,
                                         List<Result> results, List<Condition> conditions,
                                         long ttl) {

    //
    //


    /**
     * Create an empty response with all fields initiated
      */
    public CrossplaneFunctionResponse() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(), 0);
    }
}
