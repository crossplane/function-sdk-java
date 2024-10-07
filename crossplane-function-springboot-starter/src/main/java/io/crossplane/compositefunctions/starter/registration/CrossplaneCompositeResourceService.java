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

import static io.crossplane.compositefunctions.starter.registration.CrossplaneJsonSchemaGenerator.getOpenAPIV3Schema;

/**
 * Service that can register the composite resource together with a function,
 * which can also add other function to the pipeline definition
 */
public class CrossplaneCompositeResourceService {


    /**
     *  Register or update the composite resource definition using the provided client.
     *  The client needs access to register the types
     *
     * @param pipelineFunctions A list of functionnames to add to the composition pipeline
     * @param compositionDefinition The object that has the composition definiton
     * @param kubernetesClient The client to use to register the definition
     * @param <T> Must extend CustomResource
     */
    public static <T extends CustomResource<?, Void>> void registerOrUpdateCompositeResource(List<String> pipelineFunctions,
                                                                                             T compositionDefinition,
                                                                                             KubernetesClient kubernetesClient) {

        CompositeResourceDefinition compositeResourceDefinition = createCompositeResourceDefinition(compositionDefinition);

        registerOrUpdateCompositeResourceDefinition(compositeResourceDefinition, kubernetesClient);

        Composition composition = createCompositionDefinition(pipelineFunctions, compositionDefinition);

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

    /**
     * Create a CompositeResourceDefinition based on the provided CustomResource
     * If Namespaced, ClaimNames will be added in addition to Names.
     *
     * @param compositionDefinition The composition definition
     * @return A CompositeResourceDefintion based on the provided CustomResource
     * @param <T> Must extend CustomResource
     */
    public static <T extends CustomResource<?, Void>> CompositeResourceDefinition createCompositeResourceDefinition(T compositionDefinition) { //}, Class functionMixin) {

        CompositeResourceDefinition compositeResourceDefinition = new CompositeResourceDefinition();

        CompositeResourceDefinitionSpec spec = new CompositeResourceDefinitionSpec();
        spec.setGroup(compositionDefinition.getGroup());

        String namePrefix = "";

        if (compositionDefinition instanceof Namespaced) {
            ClaimNames claimNames = new ClaimNames();
            claimNames.setKind(compositionDefinition.getKind());
            claimNames.setPlural(compositionDefinition.getPlural());
            claimNames.setSingular(compositionDefinition.getSingular());
            spec.setClaimNames(claimNames);
            namePrefix = "x";
        }

        Names names = new Names();
        names.setKind(namePrefix + compositionDefinition.getKind());
        names.setPlural(namePrefix + compositionDefinition.getPlural());
        names.setSingular(namePrefix + compositionDefinition.getSingular());
        spec.setNames(names);

        Versions versions = new Versions();
        versions.setName(compositionDefinition.getVersion());

        // This is not 100%. isStorage vs referencable. Need to check the crossplane docs
        versions.setReferenceable(compositionDefinition.isStorage());
        versions.setServed(compositionDefinition.isServed());

        compositeResourceDefinition.setMetadata(CrossplaneMetadataBuilder.createMetadata(namePrefix + compositionDefinition.getCRDName()));

        Schema schema = new Schema();
        schema.setOpenAPIV3Schema(getOpenAPIV3Schema(compositionDefinition.getClass(), CrossplaneCompositeResourceMixin.class));

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
            List<String> pipelineFunctions, T compositionDefinition) {

        Composition composition = new Composition();

        composition.setMetadata(CrossplaneMetadataBuilder.createMetadata(compositionDefinition.getKind().toLowerCase() + "-composition"));
        CompositionSpec compositionSpec = new CompositionSpec();

        CompositeTypeRef compositeTypeRef = new CompositeTypeRef();
        compositeTypeRef.setKind(compositionDefinition.getKind());
        compositeTypeRef.setApiVersion(compositionDefinition.getApiVersion());

        compositionSpec.setCompositeTypeRef(compositeTypeRef);
        compositionSpec.setMode(CompositionSpec.Mode.PIPELINE);

        List<Pipeline> pipelineList = new ArrayList<>();

        pipelineFunctions.forEach(s -> pipelineList.add(createPipeline(s)));

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
