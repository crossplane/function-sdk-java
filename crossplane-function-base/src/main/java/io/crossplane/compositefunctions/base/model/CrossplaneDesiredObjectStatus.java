package io.crossplane.compositefunctions.base.model;

import io.crossplane.compositefunctions.protobuf.v1.Ready;

public enum CrossplaneDesiredObjectStatus {
    UNSPECIFIED(Ready.READY_UNSPECIFIED),
    READY(Ready.READY_TRUE),
    NOT_READY(Ready.READY_FALSE);

    private final Ready status;
    private CrossplaneDesiredObjectStatus(Ready status) {
        this.status = status;
    }

    public Ready getStatus() {
        return status;
    }

}
