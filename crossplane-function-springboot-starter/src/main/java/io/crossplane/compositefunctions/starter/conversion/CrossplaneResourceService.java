package io.crossplane.compositefunctions.starter.conversion;

import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.State;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnmarshallException;
import io.fabric8.kubernetes.client.utils.Serialization;

import java.util.Optional;

/**
 * Class with helper methods for default resources.
 */
public class CrossplaneResourceService {

    final JsonFormat.Printer printer = JsonFormat.printer();

    /**
     * Converts the incoming Composite to instance of class T
     * @param observedState The observed state from the crossplane input
     * @param clazz The class/type to create
     * @return An instance of class T from the observed state
     * @param <T> The class/type to create
     */
    public <T> Optional<T> getResource(State observedState, Class<T> clazz) {
        try {
            String resource = printer.print(observedState.getComposite().getResource());
            return Optional.ofNullable(Serialization.unmarshal(resource, clazz));
        } catch (Exception e) {
            throw new CrossplaneUnmarshallException("Error when unmarshalling the resource " + clazz.getName(), e);
        }

    }
}
