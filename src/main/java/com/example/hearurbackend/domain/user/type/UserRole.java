package com.example.hearurbackend.domain.user.type;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_PREMIUM,
    ROLE_PRIORITY;

    public boolean checkAdmin() {
        return this == ROLE_ADMIN;
    }
}
