package org.salesbind.entity;

public final class AppUser extends AggregateRoot {

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String passwordHash;
    private boolean emailVerified;

    public AppUser(String email, String firstName, String lastName, String passwordHash, boolean emailVerified) {
        this.email = email.toLowerCase().trim();
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.passwordHash = passwordHash.trim();
        this.emailVerified = emailVerified;
    }

    public static AppUser create(String email, String firstName, String lastName, String passwordHash) {
        return new AppUser(email.toLowerCase().trim(), firstName.trim(), lastName.trim(), passwordHash, false);
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
