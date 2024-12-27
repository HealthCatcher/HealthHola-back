package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.admin.SuspendUserDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.service.AdminService;
import com.example.hearurbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    public ResponseEntity<Void> suspendUser(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody SuspendUserDto suspendUserDto
    ){
        adminService.suspendUser(auth.getUsername(), suspendUserDto);
        return ResponseEntity.ok().build();
    }
}
