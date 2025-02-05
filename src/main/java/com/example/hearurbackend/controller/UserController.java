package com.example.hearurbackend.controller;

import com.example.hearurbackend.domain.experience.dto.NoticeResponseDto;
import com.example.hearurbackend.domain.oauth.dto.CustomOAuth2User;
import com.example.hearurbackend.domain.user.dto.AddressDto;
import com.example.hearurbackend.domain.user.dto.BlockUserDto;
import com.example.hearurbackend.domain.user.dto.UserDto;
import com.example.hearurbackend.domain.user.service.UserService;
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
            @RequestBody AddressDto addressDto
    ) {
        userService.changeAddress(auth.getUsername(), addressDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "주소 조회")
    @GetMapping("/address")
    public ResponseEntity<AddressDto> getAddress(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(userService.getAddress(auth.getUsername()));
    }

    @Operation(summary = "다른 사용자 차단")
    @PostMapping("/block")
    public ResponseEntity<Void> blockUser(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody BlockUserDto blockUserDto
    ) {
        userService.blockUser(auth.getUsername(), blockUserDto.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "다른 사용자 차단 해제")
    @DeleteMapping("/block")
    public ResponseEntity<Void> unblockUser(
            @AuthenticationPrincipal CustomOAuth2User auth,
            @RequestBody BlockUserDto blockUserDto
    ) {
        userService.unblockUser(auth.getUsername(), blockUserDto.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "차단한 사용자 목록 조회")
    @GetMapping("/block")
    public ResponseEntity<List<BlockUserDto>> getBlockedUserList(
            @AuthenticationPrincipal CustomOAuth2User auth
    ) {
        return ResponseEntity.ok(userService.getBlockedUserList(auth.getUsername()));
    }
}
