package org.salesbind.entity;

import java.util.UUID;

/**
 * Base class for all Aggregate Roots in the domain model.
 * <p>
 * An Aggregate Root is a fundamental concept in Domain-Driven Design (DDD). It is an
 * entity that acts as the entry point to a cluster of associated objects (the "aggregate").
 * It is responsible for maintaining the consistency and invariants of the objects within
 * its boundary.
 * </p>
 * Concrete implementations of this class should be {@code final} to prevent further
 * subclassing, ensuring that the aggregate's boundaries are well-defined.
 */
public abstract class AggregateRoot {

    private UUID id;

    /**
     * Gets the unique identifier or the aggregate root.
     *
     * @return the UUID of the aggregate.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the aggregate root.
     * <p>
     * This method is intended for internal use by persistence
     * frameworks (like JPA) and mapping libraries (like MapStruct) to reconstitute
     * an object from a data store. It should not be called directly from application code.
     *
     * @param id the UUID to set.
     */
    public void setId(UUID id) {
        this.id = id;
    }
}
