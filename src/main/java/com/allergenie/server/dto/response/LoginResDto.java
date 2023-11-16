package com.allergenie.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResDto {
    private String nickname;

    private String accessToken;

    private String refreshToken;


    @Builder
    public LoginResDto(String nickname, String accessToken , String refreshToken){
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken  = refreshToken;
    }
}
