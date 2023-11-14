package com.allergenie.server.dto.response;

import com.allergenie.server.domain.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
public class OAuthUserRes {
    private String nickname;

    private String email;

    private String profileImg;

    private String role;

    @Builder
    public OAuthUserRes(User user){
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profileImg = user.getProfileImg();
        this.role = user.getRole();
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }
}
