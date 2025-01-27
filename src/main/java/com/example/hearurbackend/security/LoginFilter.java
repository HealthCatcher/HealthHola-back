package com.example.hearurbackend.security;

import com.example.hearurbackend.domain.auth.dto.LoginDto;
import com.example.hearurbackend.domain.auth.entity.RefreshEntity;
import com.example.hearurbackend.jwt.JWTUtil;
import com.example.hearurbackend.domain.auth.repository.RefreshRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken;
        if (request.getContentType().equals(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
            try {
                LoginDto loginDTO = objectMapper.readValue(request.getReader().lines().collect(Collectors.joining()), LoginDto.class);
                authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
                return authenticationManager.authenticate(authToken);
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new AuthenticationServiceException("Request Content-Type(application/json) Parsing Error");
            }
        } else {
            String username = obtainUsername(request);
            String password = obtainPassword(request);
            authToken = new UsernamePasswordAuthenticationToken(username, password);
        }
        this.setDetails(request, authToken);


        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        String access = jwtUtil.createJwt("access", username, role, 60 * 60 * 1000 * 10L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 60 * 60 * 1000 * 24L * 30L);

        addRefreshEntity(username, refresh, 86400000L);
        
        String userPoint = String.valueOf(customUserDetails.getUserPoint());
        String nickname = customUserDetails.getNickname();
        String email = customUserDetails.getEmail();

        response.setHeader("Authorization", "bearer " + access);
        response.setHeader("refresh", "bearer " + refresh);

        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("username", username);
        responseBody.put("email", email);
        responseBody.put("nickname", nickname);
        responseBody.put("role", role);
        responseBody.put("point", userPoint);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), responseBody);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity(username, refresh, date.toString());
        refreshRepository.save(refreshEntity);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
}
