package io.crossplane.compositefunctions.starter.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.module.jsonSchema.jakarta.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.jakarta.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.jakarta.types.ArraySchema;
import io.crossplane.apiextensions.v1.compositeresourcedefinitionspec.versions.schema.OpenAPIV3Schema;
import io.fabric8.kubernetes.api.model.apiextensions.v1.JSONSchemaProps;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Generator of OpenApiV3Schema used when creating CompositeResourceDefinition
 */
public class CrossplaneJsonSchemaGenerator {

    /**
     * Create the OpenApiV3Schema
     * @param clazz The class to create the schema from
     * @param mixin A mixin to use when Jackson maps the class
     * @return A OpenAPIV3Schema based on the given class
     */
    public static OpenAPIV3Schema getOpenAPIV3Schema(Class clazz, Class mixin) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //Add mixin class to ignore id field as OpenShift does not support it.
            mapper.addMixIn(clazz, mixin);
            // apper.addMixIn(ObjectMeta.class, MetadataIgnorer.class);
            JsonSchemaGenerator generator = new JsonSchemaGenerator(mapper);
            JsonSchema jsonSchema = generator.generateSchema(clazz);

            removeIdField(jsonSchema);
            //Since JSONSchemaProps and JsonSchema is two POJOs of the same JSON schema type (specified by https://json-schema.org/specification.html)
            //We would ideally just cast like this: JSONSchemaProps props = (JSONSchemaProps) jsonSchema;
            //But that is not allowed, so we have to write as string and re-parse.
            //This was the easiest way to convert from one to the other
            String s = mapper.writeValueAsString(jsonSchema);

            return mapper.readValue(s, OpenAPIV3Schema.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate JSONSchema for class " + clazz.getSimpleName(), e);
        }
    }

    private static void removeIdField(JsonSchema jsonSchema) {
        jsonSchema.setId(null);

        if (JsonFormatTypes.OBJECT.equals(jsonSchema.getType())) {
            jsonSchema.asObjectSchema().getProperties().forEach((key, value) -> removeIdField(value));
        } else if (JsonFormatTypes.ARRAY.equals(jsonSchema.getType())) {
            final ArraySchema.Items items = jsonSchema.asArraySchema().getItems();
            if (items.isArrayItems()) {
                Stream.of(items.asArrayItems().getJsonSchemas()).forEach(s -> removeIdField(s));
            } else {
                removeIdField(items.asSingleItems().getSchema());
            }
        }

    }

    /**
     * Get the JSONSchemaProps from the class
     * @param clazz The class to get the schema from
     * @return The schemaprops based on the provided class
     */
    public static JSONSchemaProps getJsonSchema(Class clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //Add mixin class to ignore id field as OpenShift does not support it.
            mapper.addMixIn(JSONSchemaProps.class, IdIgnorer.class);
            // apper.addMixIn(ObjectMeta.class, MetadataIgnorer.class);
            JsonSchemaGenerator generator = new JsonSchemaGenerator(mapper);
            JsonSchema jsonSchema = generator.generateSchema(clazz);

            //Since JSONSchemaProps and JsonSchema is two POJOs of the same JSON schema type (specified by https://json-schema.org/specification.html)
            //We would ideally just cast like this: JSONSchemaProps props = (JSONSchemaProps) jsonSchema;
            //But that is not allowed, so we have to write as string and re-parse.
            //This was the easiest way to convert from one to the other
            String s = mapper.writeValueAsString(jsonSchema);
            return mapper.readValue(s, JSONSchemaProps.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate JSONSchema for class " + clazz.getSimpleName(), e);
        }
    }


    /**
     * Class just use to ignore the ID property that automatically gets added. 
     */
    private abstract class IdIgnorer {

        @JsonIgnore
        String id;
        @JsonIgnore abstract String getId();
        @JsonIgnore abstract void setId(java.lang.String id);
    }
}
