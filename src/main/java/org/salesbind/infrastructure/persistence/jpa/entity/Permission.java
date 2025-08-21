package org.salesbind.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions",
        indexes = {
                @Index(name = "ix_permissions_resource_action", columnList = "resource, action"),
        })
public class Permission extends AbstractPersistableCustom {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 200)
    private String resource;

    @Column(nullable = false, length = 100)
    private String action;

    private String description;

    protected Permission() {
        //
    }
}
