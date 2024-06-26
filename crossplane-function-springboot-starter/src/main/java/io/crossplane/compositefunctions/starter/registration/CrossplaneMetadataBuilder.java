package io.crossplane.compositefunctions.starter.registration;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convinience methods to create the Metadata object in any kubernetes resource
 */
public class CrossplaneMetadataBuilder {

    private CrossplaneMetadataBuilder() {
    }

    /**
     * Create metadata with name
     * @param name The name of the resource
     * @return The metdata object based on the input
     */
    public static ObjectMeta createMetadata(String name) {
        return createMetadata(name, null);
    }

    /**
     * Create metadata with name and namespace
     * @param name The name of the resource
     * @param namespace The namespace of the resource
     * @return The metdata object based on the input
     */
    public static ObjectMeta createMetadata(String name, String namespace) {
        return createMetadata(name, namespace, null);
    }

    /**
     * Create metadata with name, namespace and annotations
     * @param name The name of the resource
     * @param namespace The namespace of the resource
     * @param annotations Annotations to add to the metadata
     * @return The metdata object based on the input
     */
    public static ObjectMeta createMetadata(String name, String namespace, Map<String, String> annotations) {
        return new ObjectMetaBuilder().withName(name).withNamespace(namespace).withAnnotations(annotations).build();
    }


    /**
     * Add extra metadata to a Metadata object
     * @param annotations The extra metadata to add
     * @param objectMeta The metadata object to add it to
     * @return The metadata with the added annotations
     */
    public static ObjectMeta addAnnotations(Map<String, String> annotations, ObjectMeta objectMeta) {

        Map<String, String> existingAnnotations = objectMeta.getAnnotations();
        if (existingAnnotations == null) {
            existingAnnotations = new LinkedHashMap<>();
        }
        existingAnnotations.putAll(annotations);
        objectMeta.setAnnotations(existingAnnotations);
        return objectMeta;
    }
}
