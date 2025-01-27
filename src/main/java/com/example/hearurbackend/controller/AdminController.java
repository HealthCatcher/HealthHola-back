package com.example.hearurbackend.controller;

import com.example.hearurbackend.domain.user.dto.admin.SuspendUserDto;
import com.example.hearurbackend.domain.oauth.dto.CustomOAuth2User;
import com.example.hearurbackend.domain.user.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "유저 정지")
    @PutMapping("/suspend")
    public ResponseEntity<Void> suspendUser(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody SuspendUserDto suspendUserDto
    ){
        adminService.suspendUser(auth.getUsername(), suspendUserDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 정지 해제")
    @PutMapping("/unsuspend")
    public ResponseEntity<Void> unsuspendUser(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody SuspendUserDto suspendUserDto
    ){
        adminService.unsuspendUser(auth.getUsername(), suspendUserDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 삭제")
    @PutMapping("/account/delete")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody SuspendUserDto suspendUserDto
    ){
        adminService.deleteUser(auth.getUsername(), suspendUserDto);
        return ResponseEntity.ok().build();
    }

}
