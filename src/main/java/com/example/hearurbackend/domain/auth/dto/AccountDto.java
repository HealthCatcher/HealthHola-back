package com.example.hearurbackend.domain.auth.dto;

import com.example.hearurbackend.domain.user.type.UserRole;
import lombok.Getter;

@Getter
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
