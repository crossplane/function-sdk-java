package io.crossplane.compositefunctions.base;

import io.crossplane.compositefunctions.protobuf.Credentials;
import io.crossplane.compositefunctions.protobuf.Resources;
import io.crossplane.compositefunctions.protobuf.State;

import java.util.Map;

/**
 * Holder for the request from crossplane
 * @param observedState The observedstate of the crossplane resources
 * @param extraResourcesMap A map of any extra resources requested
 * @param credentialsMap A map of credentials sent as input
 * @param desiredState The sum of previously called functions state. To override, create a new resource with the same name in the response
 */
public record CrossplaneFunctionRequest(State observedState, Map<String, Resources> extraResourcesMap,
                                        Map<String, Credentials> credentialsMap, State desiredState) {

}
