package com.example.hearurbackend.controller;

import com.example.hearurbackend.domain.auth.repository.RefreshRepository;
import com.example.hearurbackend.domain.user.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
@RequiredArgsConstructor
public class ReissueController {
    private final AuthService authService;
    private final RefreshRepository refreshRepository;
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = request.getHeader("refresh");

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        try {
            String[] tokens = authService.reissue(refresh);
            response.addHeader("Authorization", "bearer " + tokens[0]);
            response.addHeader("refresh", "bearer " + tokens[1]);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
