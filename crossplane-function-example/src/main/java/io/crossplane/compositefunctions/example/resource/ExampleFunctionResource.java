package io.crossplane.compositefunctions.example.resource;

import io.crossplane.compositefunctions.base.CrossplaneCompositeFunctionBase;
import io.crossplane.compositefunctions.base.CrossplaneFunctionRequest;
import io.crossplane.compositefunctions.base.CrossplaneFunctionResponse;
import io.crossplane.compositefunctions.example.model.XBuckets;
import io.crossplane.compositefunctions.starter.conversion.CrossplaneResourceService;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@GrpcService
public class ExampleFunctionResource extends CrossplaneCompositeFunctionBase {

    final CrossplaneResourceService crossplaneResourceService;

    @Autowired
    public ExampleFunctionResource(CrossplaneResourceService crossplaneResourceService) {
        this.crossplaneResourceService = crossplaneResourceService;
    }


    @Override
    public CrossplaneFunctionResponse runFunction(CrossplaneFunctionRequest crossplaneFunctionRequest) {
        Optional<XBuckets> xBuckets = crossplaneResourceService.getResource(crossplaneFunctionRequest.observedState(), XBuckets.class);

        return new CrossplaneFunctionResponse();
    }



}
