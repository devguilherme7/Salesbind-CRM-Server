package org.salesbind.infrastructure.persistence.jpa.entity;

import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

import org.springframework.data.domain.Persistable;

/**
 * An abstract base class for JPA entities that provides an implementation of
 * {@link Persistable} interface.
 * <p>
 * This class helps to optimize the behaviour of Spring Data JPA's {@code save()} method.
 * By implementing {@link Persistable#isNew()}, we can explicitly tell the persistence
 * provider whether an entity is new or not, avoiding an extra database {@code SELECT}
 * statement to check for the existence of the entity before an {@code INSERT}.
 * </p>
 * The state is managed by a transient {@code isNew} flag, which is set fo {@code false}
 * after the entity is first persisted or when it is loaded from the database, using
 * JPA lifecycle callbacks ({@link PrePersist} and {@link PostLoad}).
 */
@MappedSuperclass
public abstract class AbstractPersistableCustom implements Persistable<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PrePersist
    @PostLoad
    public void markNowNew() {
        this.isNew = false;
    }
}
