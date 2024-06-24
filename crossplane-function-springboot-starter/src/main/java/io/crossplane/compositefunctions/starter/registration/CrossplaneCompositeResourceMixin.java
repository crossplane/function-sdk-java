package io.crossplane.compositefunctions.starter.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.fabric8.kubernetes.api.model.ObjectMeta;

public abstract class CrossplaneCompositeResourceMixin {

    @JsonIgnore
    String id;
    @JsonIgnore
    ObjectMeta metadata;
    @JsonIgnore
    String apiVersion = "";

    @JsonIgnore
    String kind = "";

    @JsonIgnore
    Void status;

    @JsonIgnore
    abstract ObjectMeta getMetadata();



    @JsonIgnore
    abstract void setMetadata(ObjectMeta metadata);

    @JsonIgnore
    abstract String getApiVersion();

    @JsonIgnore
    abstract void setApiVersion(String apiVersion);

    @JsonIgnore
    abstract String getKind();

    @JsonIgnore
    abstract void setKind(String kind);

    @JsonIgnore abstract String getId();
    @JsonIgnore abstract void setId(String id);
}
