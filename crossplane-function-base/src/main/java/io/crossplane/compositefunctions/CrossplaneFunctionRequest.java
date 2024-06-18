package io.crossplane.compositefunctions;

import io.crossplane.compositefunctions.protobuf.Credentials;
import io.crossplane.compositefunctions.protobuf.Resources;
import io.crossplane.compositefunctions.protobuf.State;

import java.util.Map;

/**
 * Holder for the request from crossplane
 * @param observedState The observedstate of the crossplane resources
 * @param extraResourcesMap A map of any extra resources requested
 * @param credentialsMap A map of credentials sent as input
 */
public record CrossplaneFunctionRequest(State observedState,
                                        Map<String, Resources> extraResourcesMap,
                                        Map<String, Credentials> credentialsMap) {
}
