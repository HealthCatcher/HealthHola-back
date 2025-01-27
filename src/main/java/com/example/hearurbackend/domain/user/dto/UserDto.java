package com.example.hearurbackend.domain.user.dto;

import com.example.hearurbackend.domain.user.type.UserRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private String username;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private UserRole role;
    private String address;
}
