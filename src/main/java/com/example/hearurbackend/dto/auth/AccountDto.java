package com.example.hearurbackend.dto.auth;

import com.example.hearurbackend.domain.UserRole;

public class AccountDto {
    private String username;
    private String nickname;
    private UserRole role;
    private int point;

    public AccountDto(String username, String nickname, UserRole role, int point) {
        this.username = username;
        this.nickname = nickname;
        this.role = UserRole.ROLE_USER;
        this.point = 0;
    }
}
