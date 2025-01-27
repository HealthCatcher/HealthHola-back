package com.example.hearurbackend.domain.oauth.service;


import com.example.hearurbackend.domain.oauth.dto.*;
import com.example.hearurbackend.domain.user.type.UserRole;
import com.example.hearurbackend.domain.user.dto.UserDto;
import com.example.hearurbackend.domain.user.entity.User;
import com.example.hearurbackend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;
        switch (registrationId) {
            case "naver" -> oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            case "google" -> oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            case "kakao" -> oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            default -> {
                return null;
            }
        }
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existData = userRepository.findById(username).orElse(null);
        UserDto userDTO = new UserDto();
        if (existData == null) {
            User userEntity = new User();
            userEntity.createOAuthUser(
                    username,
                    oAuth2Response.getEmail(),
                    oAuth2Response.getName(),
                    UserRole.ROLE_USER
            );
            userRepository.save(userEntity);

            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(UserRole.ROLE_USER);
        } else {
            existData.updateOAuthUser(
                    oAuth2Response.getEmail(),
                    oAuth2Response.getName()
            );
            userRepository.save(existData);

            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRole());
        }
        return new CustomOAuth2User(userDTO);
    }
}
