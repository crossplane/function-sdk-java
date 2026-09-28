package io.crossplane.compositefunctions.starter.conversion;

import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.v1.Schema;
import io.crossplane.compositefunctions.protobuf.v1.SchemaSelector;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnmarshallException;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Class that helps with the required schemas map and also to create SchemaSelector in order to get required schemas
 * to the function
 *
 *
 * @since 2.2
 */
public class CrossplaneRequiredSchemasService {

    private static final Logger logger = LoggerFactory.getLogger(CrossplaneRequiredSchemasService.class);
    private final JsonFormat.Printer printer = JsonFormat.printer();


    public <T> Optional<T> getRequiredSchema(Map<String, Schema> requiredSchemas, String resourceName, Class<T> clazz) {


        Schema schema = requiredSchemas.get(resourceName);
        Optional<T> result = Optional.empty();
        if (schema != null && schema.getOpenapiV3() != null) {
            try {
                logger.debug("We have an extra resource " + clazz.getSimpleName());
                result = Optional.ofNullable(Serialization.unmarshal(printer.print(schema.getOpenapiV3()), clazz));
            } catch (Exception e) {
                throw new CrossplaneUnmarshallException("Error when unmarshalling the required schema into " + clazz.getName(), e);
            }
        }
        return result;
    }

    public Map<String, SchemaSelector> createRequiredSchemasSelector(String resourceName, HasMetadata type) {
        SchemaSelector resourceSelector = SchemaSelector.newBuilder()
                .setApiVersion(type.getApiVersion())
                .setKind(type.getKind())
                .build();

        return Map.of(resourceName, resourceSelector);
    }


}
