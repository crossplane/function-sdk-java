package io.crossplane.compositefunctions.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.Resource;

/**
 * Helper class for converting Java Objects into protobuf Structs within the Resource object
 */
public final class CrossplaneObjectToProtobufConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonFormat.Parser parser = JsonFormat.parser();

    /**
     * Convert a java object to a resource
     * @param object The object to convert
     * @return The Resource with the object wrapped as a Struct
     */
    public static Resource convertToResource(Object object) {
        try {
            Resource.Builder builder = Resource.newBuilder();
            return builder.setResource(convertToStruct(object)).build();
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert object to resource", e);
        }

    }

    /**
     * Convert a java object to a struct
     * @param object The object to convert
     * @return The object as a protobuf struct
     */
    public static Struct convertToStruct(Object object) {
        try {
            Struct.Builder structBuilder = Struct.newBuilder();
            parser.merge(objectMapper.writeValueAsString(object), structBuilder);
            return structBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert object to struct", e);
        }

    }
}
