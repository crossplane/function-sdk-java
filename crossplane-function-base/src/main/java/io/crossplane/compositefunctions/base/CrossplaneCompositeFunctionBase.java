package io.crossplane.compositefunctions.base;


import io.crossplane.compositefunctions.protobuf.v1.FunctionRunnerServiceGrpc;
import io.crossplane.compositefunctions.protobuf.v1.Requirements;
import io.crossplane.compositefunctions.protobuf.v1.RunFunctionRequest;
import io.crossplane.compositefunctions.protobuf.v1.RunFunctionResponse;
import io.crossplane.compositefunctions.protobuf.v1.State;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The base class for a function to implement. Extend this class and implement the abstract runFunction method.
 * This class adds any desired resources from a previously called function into the returned desired map.
 * Any returned objects from the runFunction in the desiredresources map will be converted to protobuf resources,
 * so the implemented method can work with the regular Java objects.
 */
public abstract class CrossplaneCompositeFunctionBase extends FunctionRunnerServiceGrpc.FunctionRunnerServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(CrossplaneCompositeFunctionBase.class);

    @Override
    public void runFunction(RunFunctionRequest request, StreamObserver<RunFunctionResponse> responseObserver) {
        try {

            State.Builder desiredBuilder = State.newBuilder();
            State desired = request.getDesired();

            // Copy existing state into new desired state
            desiredBuilder.putAllResources(desired.getResourcesMap());

            CrossplaneFunctionRequest crossplaneFunctionRequest = new CrossplaneFunctionRequest(request.getObserved(),
                    request.getExtraResourcesMap(), request.getCredentialsMap(),  request.getDesired());


            logger.debug("Calling method with implemented logic");
            CrossplaneFunctionResponse crossplaneFunctionResponse = runFunction(crossplaneFunctionRequest);
            logger.debug("Finished calling method with implemented logic");

            for (Map.Entry<String, Object> entry : crossplaneFunctionResponse.desiredResources().entrySet()) {
                desiredBuilder.putResources(entry.getKey(), CrossplaneObjectToProtobufConverter.convertToResource(entry.getValue()));
            }

            RunFunctionResponse.Builder responseBuilder = RunFunctionResponse.newBuilder();


            if (! crossplaneFunctionResponse.resourceSelectors().isEmpty()) {
                Requirements requirements = Requirements.newBuilder()
                        .putAllExtraResources(crossplaneFunctionResponse.resourceSelectors())
                        .build();
                responseBuilder.setRequirements(requirements);
            }

            if (! crossplaneFunctionResponse.results().isEmpty()) {
                responseBuilder.addAllResults(crossplaneFunctionResponse.results());
            }

            if (! crossplaneFunctionResponse.conditions().isEmpty()) {
                responseBuilder.addAllConditions(crossplaneFunctionResponse.conditions());
            }

            if (desiredBuilder.getResourcesCount() > 0) {
                responseBuilder.setDesired(desiredBuilder.build());
            }

            RunFunctionResponse runFunctionResponse = responseBuilder
                    .build();

            responseObserver.onNext(runFunctionResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error when running function logic", e);
            responseObserver.onError(e);
        }

    }

    /**
     * The main method where the logic should live.
     * @param crossplaneFunctionRequest The request object with the inputs from Crossplane added to it
     * @return The response with desired resources, resource selectors and function results
     */
    public abstract CrossplaneFunctionResponse runFunction(CrossplaneFunctionRequest crossplaneFunctionRequest);

}
