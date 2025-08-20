package org.salesbind.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "organizations", indexes = {
        @Index(name = "ix_organizations_name", columnList = "name")
})
public class Organization extends AbstractPersistableCustom {

    @Column(nullable = false)
    private String name;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrganizationMember> members = new ArrayList<>();

    public Organization(String name) {
        this.name = name;
    }

    public static Organization create(String name) {
        return new Organization(name);
    }

    protected Organization() {
        //
    }

    public void addMember(OrganizationMember member) {
        members.add(member);
        member.setOrganization(this);
    }
}
