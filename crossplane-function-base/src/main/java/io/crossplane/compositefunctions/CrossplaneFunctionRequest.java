package io.crossplane.compositefunctions;

import io.crossplane.compositefunctions.protobuf.Credentials;
import io.crossplane.compositefunctions.protobuf.Resources;
import io.crossplane.compositefunctions.protobuf.State;

import java.util.Map;

public record CrossplaneFunctionRequest(State observedState,
                                        Map<String, Resources> extraResourcesMap,
                                        Map<String, Credentials> credentialsMap) {
}
