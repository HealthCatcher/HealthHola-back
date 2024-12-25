package com.example.hearurbackend.config;

import com.example.hearurbackend.jwt.JWTFilter;
import com.example.hearurbackend.jwt.JWTUtil;
import com.example.hearurbackend.oauth2.CustomClientRegistrationRepo;
import com.example.hearurbackend.oauth2.CustomSuccessHandler;
import com.example.hearurbackend.security.LoginFilter;
import com.example.hearurbackend.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          CustomSuccessHandler customSuccessHandler,
                          CustomClientRegistrationRepo customClientRegistrationRepo,
                          JWTUtil jwtUtil,
                          AuthenticationConfiguration authenticationConfiguration
    ) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.customClientRegistrationRepo = customClientRegistrationRepo;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //cors 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

                    CorsConfiguration configuration = new CorsConfiguration();

                    // Origin을 올바르게 설정
                    configuration.setAllowedOrigins(Arrays.asList("http://localhost:8081", "http://localhost:3000"));

                    // HTTP 메서드를 올바르게 설정
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

                    configuration.setAllowCredentials(true);

                    // Allowed headers 설정
                    configuration.setAllowedHeaders(Arrays.asList(
                            "Origin",
                            "Accept",
                            "X-Requested-With",
                            "Content-Type",
                            "Access-Control-Request-Method",
                            "Access-Control-Request-Headers",
                            "Authorization"
                    ));

                    // MaxAge 설정
                    configuration.setMaxAge(3600L);

                    // Exposed headers 설정
                    List<String> exposedHeaders = Arrays.asList("Set-Cookie", "Authorization", "Location");
                    configuration.setExposedHeaders(exposedHeaders);

                    return configuration;
                }));

        //csrf disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        //From 로그인 방식 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .clientRegistrationRepository(customClientRegistrationRepo.clientRegistrationRepository())
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler));

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/v1/auth/signup").permitAll()
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/jwt").permitAll()
                        .requestMatchers("/api/v1/auth/email/send").permitAll()
                        .requestMatchers("/api/v1/auth/email/verify").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/post/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/community/post/*/comment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/experience/notice").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/experience/notice/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/experience/review").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/experience/review/*").permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}