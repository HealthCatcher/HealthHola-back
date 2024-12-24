package com.example.hearurbackend.domain;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    public boolean checkAdmin() {
        return this == ROLE_ADMIN;
    }
}
