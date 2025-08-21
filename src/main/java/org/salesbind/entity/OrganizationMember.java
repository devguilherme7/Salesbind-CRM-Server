package org.salesbind.entity;

public final class OrganizationMember extends AggregateRoot {

    private final Organization organization;
    private final AppUser user;

    public OrganizationMember(Organization organization, AppUser user) {
        this.organization = organization;
        this.user = user;
    }

    public static OrganizationMember create(Organization organization, AppUser user) {
        return new OrganizationMember(organization, user);
    }

    public AppUser getUser() {
        return user;
    }

    public Organization getOrganization() {
        return organization;
    }
}
