package io.crossplane.compositefunctions.starter.conversion;


import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.v1.Resource;
import io.crossplane.compositefunctions.protobuf.v1.State;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnmarshallException;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class with helper methods for observable resources.
 */
public class CrossplaneObservableService {

    private static final Logger logger = LoggerFactory.getLogger(CrossplaneObservableService.class);
    private final JsonFormat.Printer printer = JsonFormat.printer();

    /**
     * Retrieve a resource from the observedstate and convert in into class T
     * @param resourceName The resource name to find in the observed state
     * @param observedState The observed state from the crossplane input
     * @param clazz The class/type to create
     * @return An instance of class T from the observed state
     * @param <T> The class/type to create
     */
    public <T> Optional<T> getObservableResource(String resourceName, State observedState, Class<T> clazz) {
        Resource observedResource = observedState.getResourcesOrDefault(resourceName, null);
        Optional<T> result = Optional.empty();
        if (observedResource != null) {
            try {
                logger.debug("We have an observed " + clazz.getSimpleName());
                result = Optional.ofNullable(Serialization.unmarshal(printer.print(observedResource.getResource()), clazz));
            } catch (Exception e) {

                throw new CrossplaneUnmarshallException("Error when unmarshalling the observed resource " + clazz.getName(), e);
            }
        }
        return result;
    }

    public Map<String, String> getObservableConnectionDetails(String resourceName, State observedState) {
        Resource observedResource = observedState.getResourcesOrDefault(resourceName, null);
        Map<String, String> result = new HashMap<>();
        if (observedResource != null) {
            try {
                logger.debug("We have an observed connectionDetails for " + resourceName);
                observedResource.getConnectionDetailsMap().forEach((key, value) ->
                        result.put(key, value.toStringUtf8())
                );
            } catch (Exception e) {

                throw new CrossplaneUnmarshallException("Error when unmarshalling the connectionDetails for " + resourceName, e);
            }
        }
        return result;
    }


}
