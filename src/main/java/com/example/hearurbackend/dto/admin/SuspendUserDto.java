package com.example.hearurbackend.dto.admin;

import lombok.Getter;

@Getter
public class SuspendUserDto {
    private String username;
    private int days;
}
