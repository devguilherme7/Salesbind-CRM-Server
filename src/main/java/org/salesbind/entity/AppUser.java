package org.salesbind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "app_users")
public class AppUser extends AbstractPersistableCustom {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean emailVerified;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public AppUser(String email, String firstName, String lastName, String passwordHash) {
        this.email = email.toLowerCase().trim();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.passwordHash = passwordHash.trim();
        this.emailVerified = false;
    }

    protected AppUser() {
        //
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
