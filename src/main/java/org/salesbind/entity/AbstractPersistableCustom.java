package org.salesbind.entity;

import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class AbstractPersistableCustom implements Persistable<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Transient
    private boolean new_ = true;

    @Override
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return this.new_;
    }

    @PrePersist
    @PostLoad
    public void markNowNew() {
        this.new_ = false;
    }
}
