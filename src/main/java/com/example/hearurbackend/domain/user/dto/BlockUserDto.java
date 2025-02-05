package com.example.hearurbackend.domain.user.dto;

import lombok.Getter;

@Getter
public class BlockUserDto {
    private String username;

    public BlockUserDto(String username) {
        this.username = username;
    }
}
