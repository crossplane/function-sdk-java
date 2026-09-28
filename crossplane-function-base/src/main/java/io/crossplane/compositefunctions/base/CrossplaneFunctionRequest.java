package io.crossplane.compositefunctions.base;

import io.crossplane.compositefunctions.protobuf.v1.Credentials;
import io.crossplane.compositefunctions.protobuf.v1.RequestMeta;
import io.crossplane.compositefunctions.protobuf.v1.Resources;
import io.crossplane.compositefunctions.protobuf.v1.Schema;
import io.crossplane.compositefunctions.protobuf.v1.State;

import java.util.Map;

/**
 * Holder for the request from crossplane
 * @param requestMeta Holds the tag of the request and the capabilities of the crossplane version running
 * @param observedState The observedstate of the crossplane resources
 * @param requiredResourcesMap A map of any required resources requested, either dynamic or bootstrap resources
 * @param requiredSchemasMap A map of any required schemas requested
 * @param credentialsMap A map of credentials sent as input
 * @param desiredState The sum of previously called functions state. To override, create a new resource with the same name in the response
 */
public record CrossplaneFunctionRequest(RequestMeta requestMeta, State observedState, Map<String, Resources> requiredResourcesMap,
                                        Map<String, Schema> requiredSchemasMap, Map<String, Credentials> credentialsMap, State desiredState) {

}
