package io.crossplane.compositefunctions.starter.registration;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrossplaneMetadataBuilder {

    private CrossplaneMetadataBuilder() {
    }

    public static ObjectMeta createMetadata(String name, String namespace) {
        return createMetadata(name, namespace, null);
    }

    public static ObjectMeta createMetadata(String name, String namespace, Map<String, String> annotations) {
        return new ObjectMetaBuilder().withName(name).withNamespace(namespace).withAnnotations(annotations).build();
    }


    public static ObjectMeta createMetadata(String name) {
        return createMetadata(name, null);
    }

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
