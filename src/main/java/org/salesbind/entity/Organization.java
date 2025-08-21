package org.salesbind.entity;

public final class Organization extends AggregateRoot {

    private final String name;

    public Organization(String name) {
        this.name = name;
    }

    public static Organization create(String name) {
        return new Organization(name);
    }

    public String getName() {
        return name;
    }
}
