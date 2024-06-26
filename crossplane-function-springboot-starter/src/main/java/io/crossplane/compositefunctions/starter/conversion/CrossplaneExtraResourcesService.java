package io.crossplane.compositefunctions.starter.conversion;

import com.google.protobuf.util.JsonFormat;

import io.crossplane.compositefunctions.protobuf.ResourceSelector;
import io.crossplane.compositefunctions.protobuf.Resources;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnexpectedItemsException;
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
 * Class that helps with the extra resources map and also to create ResourceSelector in order to get extra resources
 * to the function
 *
 * Commented out in 1.14
 *
 * @since 1.15
 */
public class CrossplaneExtraResourcesService {

    private static final Logger logger = LoggerFactory.getLogger(CrossplaneExtraResourcesService.class);
    private final JsonFormat.Printer printer = JsonFormat.printer();


    public <T> Optional<T> getExtraResource(Map<String, Resources> extraResources, String resourceName, Class<T> clazz) {
        return getExtraResources(extraResources, resourceName, 1, clazz).get(0);
    }

    public <T> List<Optional<T>> getExtraResources(Map<String, Resources> extraResources, String resourceName, int expectedResources, Class<T> clazz) {
        List<Optional<T>> result = new ArrayList<>();
        Resources resources = extraResources.get(resourceName);

        if (resources != null ) {
            if (resources.getItemsCount() != expectedResources) {
                throw new CrossplaneUnexpectedItemsException("Unexpected number of resources. Expected " + expectedResources + " but got " + resources.getItemsCount() + ".");
            }
            for (int i = 0; i < expectedResources; i++) {
                try {
                    logger.debug("We have an extra resource " + clazz.getSimpleName());
                    result.add(Optional.ofNullable(Serialization.unmarshal(printer.print(resources.getItems(i).getResource()), clazz)));
                } catch (Exception e) {
                    throw new CrossplaneUnmarshallException("Error when unmarshalling the extra resource " + clazz.getName(), e);
                }
            }

        }
        return result;
    }

    public Map<String, ResourceSelector> createExtraResourcesSelector(String resourceName, HasMetadata type) {
        ResourceSelector resourceSelector = ResourceSelector.newBuilder()
                .setApiVersion(type.getApiVersion())
                .setKind(type.getKind())
                .setMatchName(resourceName)
                .build();

        return Map.of(resourceName, resourceSelector);
    }


}
