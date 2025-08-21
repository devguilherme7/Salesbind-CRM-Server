package org.salesbind.entity;

import java.util.UUID;

public abstract class AggregateRoot {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
