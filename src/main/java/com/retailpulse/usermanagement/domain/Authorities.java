package com.retailpulse.usermanagement.domain;

public enum Authorities {
    ADMIN("ADMIN"),
    CASHIER("CASHIER"),
    MANAGER("MANAGER");

    private final String authority;

    Authorities(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public static Authorities fromString(String authority) {
        for (Authorities a : Authorities.values()) {
            if (a.authority.equalsIgnoreCase(authority)) {
                return a;
            }
        }
        throw new IllegalArgumentException("No authority with name " + authority + " found");
    }

}
