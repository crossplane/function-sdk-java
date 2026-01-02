package io.crossplane.compositefunctions.starter.conversion;

import com.google.protobuf.util.JsonFormat;
import io.crossplane.compositefunctions.protobuf.v1.ResourceSelector;
import io.crossplane.compositefunctions.protobuf.v1.Resources;
import io.crossplane.compositefunctions.starter.exception.CrossplaneUnmarshallException;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Class that helps with the required resources map and also to create ResourceSelector in order to get required resources
 * to the function
 *
 *
 * @since 2.0
 */
public class CrossplaneRequiredResourcesService {

    private static final Logger logger = LoggerFactory.getLogger(CrossplaneRequiredResourcesService.class);
    private final JsonFormat.Printer printer = JsonFormat.printer();


    public <T> Optional<T> getRequiredResource(Map<String, Resources> requiredResources, String resourceName, Class<T> clazz) {
        List<Optional<T>> resources = getRequiredResources(requiredResources, resourceName, 1, clazz);

        if (resources.isEmpty()) {
            return Optional.empty();
        }
        return resources.get(0);
    }

    public <T> List<Optional<T>> getRequiredResources(Map<String, Resources> requiredResources, String resourceName, int expectedResources, Class<T> clazz) {
        List<Optional<T>> result = new ArrayList<>();
        Resources resources = requiredResources.get(resourceName);

        if (resources != null &&  resources.getItemsCount() == expectedResources) {
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

    public Map<String, String> getConnectionDetails(Map<String, Resources> requiredResources, String resourceName) {
        List<Map<String, String>> resources = getConnectionDetails(requiredResources, resourceName, 1);

        if (resources.isEmpty()) {
            return new HashMap<>();
        }
        return resources.get(0);
    }

    public List<Map<String, String>> getConnectionDetails(Map<String, Resources> requiredResources, String resourceName, int expectedResources) {
        List<Map<String, String>> result = new ArrayList<>();
        Resources resources = requiredResources.get(resourceName);

        if (resources != null &&  resources.getItemsCount() == expectedResources) {
            for (int i = 0; i < expectedResources; i++) {
                try {
                    logger.debug("We have connectiondetails " + resourceName);
                    Map<String, String> currentDetails = new HashMap<>();
                    resources.getItems(i).getConnectionDetailsMap().forEach((key, value) ->
                            currentDetails.put(key, value.toStringUtf8())
                    );
                    result.add(currentDetails);
                } catch (Exception e) {
                    throw new CrossplaneUnmarshallException("Error when unmarshalling the connectionDetails", e);
                }
            }

        }
        return result;
    }



    public Map<String, ResourceSelector> createRequiredResourcesSelector(String resourceName, HasMetadata type) {
        ResourceSelector resourceSelector = ResourceSelector.newBuilder()
                .setApiVersion(type.getApiVersion())
                .setKind(type.getKind())
                .setMatchName(resourceName)
                .build();

        return Map.of(resourceName, resourceSelector);
    }


}
