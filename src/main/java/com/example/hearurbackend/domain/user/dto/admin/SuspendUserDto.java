package com.example.hearurbackend.domain.user.dto.admin;

import lombok.Getter;

@Getter
public class SuspendUserDto {
    private String username;
    private int days;
}
