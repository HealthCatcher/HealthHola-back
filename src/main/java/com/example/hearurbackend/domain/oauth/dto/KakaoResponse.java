package com.example.hearurbackend.domain.oauth.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null) {
            return (String) kakaoAccount.get("email");
        }
        return "";
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");
        if (properties != null && properties.get("nickname") != null) {
            return properties.get("nickname").toString();
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null && profile.get("nickname") != null) {
                return profile.get("nickname").toString();
            }
        }
        return "";
    }
}
