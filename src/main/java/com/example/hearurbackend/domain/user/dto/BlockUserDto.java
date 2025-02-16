package com.example.hearurbackend.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockUserDto {
    private String username;

    public BlockUserDto(String username) {
        this.username = username;
    }
}
