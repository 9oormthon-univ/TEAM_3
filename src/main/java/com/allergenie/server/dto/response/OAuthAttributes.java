package com.allergenie.server.dto.response;

import com.allergenie.server.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private Long kakaoId;
    private String nickname;
    private String email;
    private String profileImg;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, Long kakaoId, String nickname,
                           String email, String profileImg) {
        this.kakaoId = kakaoId;
        this.attributes = attributes;
        this.nickname = nickname;
        this.email = email;
        this.profileImg = profileImg;
    }

    public static OAuthAttributes ofKakao(Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) response.get("profile");

        return OAuthAttributes.builder()
                .kakaoId((Long)attributes.get("id"))
                .nickname((String)profile.get("nickname"))
                .email((String)response.get("email"))
                .profileImg((String)profile.get("profile_image_url"))
                .attributes(attributes)
                .build();
    }

    public User toEntity() {
        User user = User.builder()
                .nickname(nickname)
                .profileImg(profileImg)
                .email(email)
                .build();

        return user;
    }
}

