package io.crossplane.compositefunctions.starter.conversion;


import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.Resource;
import io.crossplane.compositefunctions.protobuf.State;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnmarshallException;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CrossplaneObservableService {

    private static final Logger logger = LoggerFactory.getLogger(CrossplaneObservableService.class);
    private final JsonFormat.Printer printer = JsonFormat.printer();

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

}
