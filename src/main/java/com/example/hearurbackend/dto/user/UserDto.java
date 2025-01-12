package com.example.hearurbackend.dto.user;

import com.example.hearurbackend.domain.UserRole;
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
