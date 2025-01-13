package com.example.hearurbackend.controller;

import com.example.hearurbackend.dto.auth.AuthRequest;
import com.example.hearurbackend.dto.auth.EmailDto;
import com.example.hearurbackend.dto.auth.RegisterOauthDto;
import com.example.hearurbackend.dto.oauth.CustomOAuth2User;
import com.example.hearurbackend.dto.user.UserDto;
import com.example.hearurbackend.entity.user.User;
import com.example.hearurbackend.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @Operation(summary = "안드로이드 앱 소셜 로그인 처리")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) throws JsonProcessingException {

        String token = authService.mobileLogin(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return ResponseEntity.ok().headers(responseHeaders).body("{\"code\": \"200\"}");
    }

    @Operation(summary = "OAuth2 로그인시 jwt 위치 쿠키 -> 헤더 메소드")
    @GetMapping("/jwt")
    public ResponseEntity<?> transferJwtFromCookieToHeader(HttpServletRequest request, HttpServletResponse response) {
        String token = authService.transferJwtFromCookieToHeader(request, response);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return ResponseEntity.ok().headers(headers).build();
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> register(
            @RequestBody UserDto userDTO
    ) {
        //실제 서비스하면 주석 해제
//            if(!authService.isVerified(userDTO.getUsername())) {
//                return ResponseEntity.badRequest().body("이메일 인증을 해주세요.");
//            }
        User newUser = authService.registerUser(userDTO);
        return ResponseEntity.created(URI.create("/users/" + newUser.getUsername())).build();

    }

    @Operation(summary = "비밀번호 검증")
    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyPassword(
            @RequestBody UserDto userDTO,
            @AuthenticationPrincipal CustomOAuth2User user
    ) {
        boolean isCorrect = authService.verifyPassword(user.getUsername(), userDTO.getPassword());
        return isCorrect ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "비밀번호 변경")
    @PostMapping("/password")
    public ResponseEntity<?> changePassword(
            @RequestBody UserDto userDTO
    ) {
        authService.changePassword(userDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이메일 인증 코드 전송")
    @PostMapping("/email/send")
    public ResponseEntity<?> sendMail(EmailDto emailDto) throws MessagingException {
        authService.sendEmail(emailDto.getMail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 인증 코드 확인")
    @PostMapping("/email/verify")
    public ResponseEntity<?> verify(EmailDto emailDto) {
        boolean isVerify = authService.verifyEmailCode(emailDto.getMail(), emailDto.getVerifyCode());
        return isVerify ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean isDuplicated = authService.checkEmail(email);
        return isDuplicated ? ResponseEntity.badRequest().build() : ResponseEntity.ok().build();
    }

    @Operation(summary = "OAuth2 계정 등록")
    @PostMapping("/oauth")
    public ResponseEntity<?> registerOAuthUser(
            @RequestBody RegisterOauthDto registerOauthDto,
            @AuthenticationPrincipal CustomOAuth2User user
    ) {
        User newUser = authService.registerOAuthUser(registerOauthDto, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "OAuth2 계정 등록 여부 조회")
    @GetMapping("/oauth")
    public ResponseEntity<?> checkOAuthUser(@AuthenticationPrincipal CustomOAuth2User user) {
        boolean isRegistered = authService.checkOAuthUser(user);
        return isRegistered ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
