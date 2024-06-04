package io.crossplane.compositefunctions;

import io.crossplane.compositefunctions.protobuf.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
                    request.getExtraResourcesMap(), request.getCredentialsMap());

            logger.debug("Calling method with implemented logic");
            CrossplaneFunctionResponse crossplaneFunctionResponse = runFunction(crossplaneFunctionRequest);
            logger.debug("Finished calling method with implemented logic");

            for (Map.Entry<String, Object> entry : crossplaneFunctionResponse.desiredResources().entrySet()) {
                desiredBuilder.putResources(entry.getKey(), CrossplaneObjectToProtobufConverter.convertToResource(entry.getValue()));
            }

            Requirements requirements = Requirements.newBuilder()
                    .putAllExtraResources(crossplaneFunctionResponse.resourceSelectors())
                    .build();

            RunFunctionResponse runFunctionResponse = RunFunctionResponse
                    .newBuilder()
                    .setRequirements(requirements)
                    .addAllResults(crossplaneFunctionResponse.results())
                    .setDesired(desiredBuilder.build())
                    .build();

            responseObserver.onNext(runFunctionResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error when running function logic", e);
            responseObserver.onError(e);
        }

    }

    public abstract CrossplaneFunctionResponse runFunction(CrossplaneFunctionRequest crossplaneFunctionRequest);

}
