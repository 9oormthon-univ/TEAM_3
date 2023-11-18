package com.allergenie.server.dto.request;

import com.allergenie.server.domain.Image;
import com.allergenie.server.domain.User;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFormDto {
    @NotBlank
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @Builder
    public UserFormDto(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .build();
    }

    public void setPassword(String password){
        this.password = password;
    }
}
