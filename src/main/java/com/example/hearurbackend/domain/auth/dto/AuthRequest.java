package com.example.hearurbackend.domain.auth.dto;

import lombok.Getter;

@Getter
public class AuthRequest {
    private String provider;
    private String accessToken;
}
