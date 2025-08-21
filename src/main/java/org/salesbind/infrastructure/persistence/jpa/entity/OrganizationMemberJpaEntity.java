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

import org.salesbind.entity.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "organization_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_organization_members_user", columnNames = {"organization_id", "user_id"})
        },
        indexes = {
                @Index(name = "ix_organization_members_organization", columnList = "organization_id"),
                @Index(name = "ix_organization_members_user", columnList = "user_id")
        })
public class OrganizationMemberJpaEntity extends AbstractPersistableCustom {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationJpaEntity organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private AppUserJpaEntity invitedBy;

    private LocalDateTime invitedAt;

    private LocalDateTime acceptedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // association: membership -> roles (many-to-many)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "organization_member_roles",
            joinColumns = @JoinColumn(name = "organization_member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public OrganizationJpaEntity getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationJpaEntity organization) {
        this.organization = organization;
    }

    public AppUserJpaEntity getUser() {
        return user;
    }

    public void setUser(AppUserJpaEntity user) {
        this.user = user;
    }

    public AppUserJpaEntity getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(AppUserJpaEntity invitedBy) {
        this.invitedBy = invitedBy;
    }

    public LocalDateTime getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(LocalDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
