package com.example.hearurbackend.controller;

import com.example.hearurbackend.domain.oauth.dto.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

    @Operation(summary = "내 정보")
    @GetMapping("/api/v1/my")
    public ResponseEntity<String> myAPI(@AuthenticationPrincipal CustomOAuth2User auth){

        return ResponseEntity.ok(auth.getUsername());
    }
}
