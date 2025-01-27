package com.example.hearurbackend.domain.user.dto;

import lombok.Getter;

@Getter
public class RegisterUserDto {
    private String username;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String address;
}
