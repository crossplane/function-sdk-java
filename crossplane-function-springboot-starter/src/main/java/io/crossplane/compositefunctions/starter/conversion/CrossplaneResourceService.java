package io.crossplane.compositefunctions.starter.conversion;

import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.State;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnmarshallException;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CrossplaneResourceService {

    final JsonFormat.Printer printer = JsonFormat.printer();

    public <T> Optional<T> getResource(State observedState, Class<T> clazz) {
        try {
            String resource = printer.print(observedState.getComposite().getResource());
            return Optional.ofNullable(Serialization.unmarshal(resource, clazz));
        } catch (Exception e) {
            throw new CrossplaneUnmarshallException("Error when unmarshalling the resource " + clazz.getName(), e);
        }

    }
}
