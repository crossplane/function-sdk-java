package io.crossplane.compositefunctions.starter.registration;

import io.crossplane.apiextensions.v1.CompositeResourceDefinition;
import io.crossplane.apiextensions.v1.CompositeResourceDefinitionSpec;
import io.crossplane.apiextensions.v1.Composition;
import io.crossplane.apiextensions.v1.CompositionSpec;
import io.crossplane.apiextensions.v1.compositeresourcedefinitionspec.ClaimNames;
import io.crossplane.apiextensions.v1.compositeresourcedefinitionspec.Names;
import io.crossplane.apiextensions.v1.compositeresourcedefinitionspec.Versions;
import io.crossplane.apiextensions.v1.compositeresourcedefinitionspec.versions.Schema;
import io.crossplane.apiextensions.v1.compositionspec.CompositeTypeRef;
import io.crossplane.apiextensions.v1.compositionspec.Pipeline;
import io.crossplane.apiextensions.v1.compositionspec.pipeline.FunctionRef;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;

import java.util.ArrayList;
import java.util.List;

import static io.crossplane.compositefunctions.starter.registration.CrossplanJsonSchemaGenerator.getOpenAPIV3Schema;

public class CrossplaneCompositeResourceService {



    public static <T extends CustomResource<?, Void>> void registerOrUpdateCompositeResource(String functionName,
                                                                                             List<String> additionalFunctions,
                                                                                             T functionDefinition,
                                                                                             KubernetesClient kubernetesClient) {

        CompositeResourceDefinition compositeResourceDefinition = createCompositeResourceDefinition(functionDefinition);

        registerOrUpdateCompositeResourceDefinition(compositeResourceDefinition, kubernetesClient);

        Composition composition = createCompositionDefinition(functionName, additionalFunctions, functionDefinition);

        registerOrUpdateCompositeResourceDefinition(composition, kubernetesClient);

    }

    private static void registerOrUpdateCompositeResourceDefinition(CompositeResourceDefinition compositeResourceDefinition, KubernetesClient kubernetesClient) {
        var customResourceClient = kubernetesClient.resources(CompositeResourceDefinition.class);
        Resource<CompositeResourceDefinition> resource = customResourceClient.resource(compositeResourceDefinition);

        CompositeResourceDefinition serverSide = resource.get();
        if (serverSide == null) {
            resource.create();
        } else {
            resource.update();
        }
    }

    public static <T extends CustomResource<?, Void>> CompositeResourceDefinition createCompositeResourceDefinition(T functionDefinition) { //}, Class functionMixin) {

        CompositeResourceDefinition compositeResourceDefinition = new CompositeResourceDefinition();
        compositeResourceDefinition.setMetadata(CrossplaneMetadataBuilder.createMetadata(functionDefinition.getCRDName()));

        CompositeResourceDefinitionSpec spec = new CompositeResourceDefinitionSpec();
        spec.setGroup(functionDefinition.getGroup());

        String namePrefix = "";

        if (functionDefinition instanceof Namespaced) {
            ClaimNames claimNames = new ClaimNames();
            claimNames.setKind(functionDefinition.getKind());
            claimNames.setPlural(functionDefinition.getPlural());
            claimNames.setSingular(functionDefinition.getSingular());
            spec.setClaimNames(claimNames);
            namePrefix = "x";
        }

        Names names = new Names();
        names.setKind(namePrefix + functionDefinition.getKind());
        names.setPlural(namePrefix + functionDefinition.getPlural());
        names.setSingular(namePrefix + functionDefinition.getSingular());
        spec.setNames(names);

        Versions versions = new Versions();
        versions.setName(functionDefinition.getVersion());

        // This is not 100%. isStorage vs referencable. Need to check the crossplan docs
        versions.setReferenceable(functionDefinition.isStorage());
        versions.setServed(functionDefinition.isServed());


        Schema schema = new Schema();
        schema.setOpenAPIV3Schema(getOpenAPIV3Schema(functionDefinition.getClass(), CrossplaneCompositeResourceMixin.class));

        versions.setSchema(schema);
        spec.setVersions(List.of(versions));
        compositeResourceDefinition.setSpec(spec);
        return compositeResourceDefinition;
    }


    private static void registerOrUpdateCompositeResourceDefinition(Composition compositeResourceDefinition, KubernetesClient kubernetesClient) {
        var customResourceClient = kubernetesClient.resources(Composition.class);
        Resource<Composition> resource = customResourceClient.resource(compositeResourceDefinition);

        Composition serverSide = resource.get();
        if (serverSide == null) {
            resource.create();
        } else {
            resource.update();
        }
    }

    private static <T extends CustomResource<?, Void>> Composition createCompositionDefinition(
            String functionName, List<String> additionalFunctions,
            T functionDefinition) {

        Composition composition = new Composition();

        composition.setMetadata(CrossplaneMetadataBuilder.createMetadata(functionDefinition.getKind().toLowerCase() + "-composition"));
        CompositionSpec compositionSpec = new CompositionSpec();

        CompositeTypeRef compositeTypeRef = new CompositeTypeRef();
        compositeTypeRef.setKind(functionDefinition.getKind());
        compositeTypeRef.setApiVersion(functionDefinition.getApiVersion());

        compositionSpec.setCompositeTypeRef(compositeTypeRef);
        compositionSpec.setMode(CompositionSpec.Mode.PIPELINE);

        List<Pipeline> pipelineList = new ArrayList<>();

        pipelineList.add(createPipeline(functionName));

        additionalFunctions.forEach(s -> pipelineList.add(createPipeline(s)));

        compositionSpec.setPipeline(pipelineList);
        composition.setSpec(compositionSpec);
        return composition;
    }

    private static Pipeline createPipeline(String pipelineName) {
        Pipeline pipeline = new Pipeline();
        pipeline.setStep(pipelineName);
        FunctionRef functionRef = new FunctionRef();
        functionRef.setName(pipelineName);
        pipeline.setFunctionRef(functionRef);
        return pipeline;
    }

}
