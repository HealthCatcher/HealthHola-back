package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.experience.NoticeResponseDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.user.UserDto;
import com.example.hearurbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "별명 변경")
    @PutMapping("/nickname")
    public ResponseEntity<Void> changeNickname(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody UserDto userDTO
    ) {
        userService.changeNickname(auth.getUsername(), userDTO.getNickname());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내가 찜한 체험단 목록 조회")
    @GetMapping("/favorite-notice")
    public ResponseEntity<List<NoticeResponseDto>> getLikeList(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(userService.getFavoriteNoticeList(auth.getUsername()));
    }

    @Operation(summary = "내가 신청한 체험단 목록 조회")
    @GetMapping("/applied-notice")
    public ResponseEntity<List<NoticeResponseDto>> getAppliedList(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(userService.getAppliedNoticeList(auth.getUsername()));
    }

    @Operation(summary = "주소지 설정")
    @PutMapping("/address")
    public ResponseEntity<Void> changeAddress(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody UserDto userDTO
    ) {
        userService.changeAddress(auth.getUsername(), userDTO.getAddress());
        return ResponseEntity.noContent().build();
    }
}
