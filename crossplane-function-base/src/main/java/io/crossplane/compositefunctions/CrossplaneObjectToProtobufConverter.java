package io.crossplane.compositefunctions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.Resource;


public class CrossplaneObjectToProtobufConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final JsonFormat.Parser parser = JsonFormat.parser();

    public static Resource convertToResource(Object object) {
        try {
            Resource.Builder builder = Resource.newBuilder();
            return builder.setResource(convertToStruct(object)).build();
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert object to resource", e);
        }

    }

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
