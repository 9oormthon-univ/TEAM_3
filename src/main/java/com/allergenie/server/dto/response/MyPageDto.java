package com.allergenie.server.dto.response;

import com.allergenie.server.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyPageDto {
    private String userImageURL;
    private String nickname;
    private String email;

    private List<ProhibitionDto> prohibitionList;

    @Builder
    public MyPageDto(User user, List<ProhibitionDto> prohibitionList) {
        this.userImageURL = user.getImageUrl();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.prohibitionList = prohibitionList;
    }
}
