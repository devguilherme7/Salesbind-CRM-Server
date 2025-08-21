package org.salesbind.infrastructure.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_roles_name_org", columnNames = {"name", "organization_id"})
        },
        indexes = {
                @Index(name = "ix_roles_organization", columnList = "organization_id")
        })
public class Role extends AbstractPersistableCustom {

    @Column(nullable = false, length = 60)
    private String name;

    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * If organization is null -> the role is global (system role).
     * If set -> the role is scoped to that organization (custom role).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationJpaEntity organization;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Role() {
        //
    }
}
