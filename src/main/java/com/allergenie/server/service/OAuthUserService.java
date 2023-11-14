package com.allergenie.server.service;

import com.allergenie.server.domain.User;
import com.allergenie.server.dto.response.OAuthAttributes;
import com.allergenie.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuthUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    //회원가입 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        try{
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService(); //객체생성
            OAuth2User oAuth2User = delegate.loadUser(userRequest);// Oath2 정보를 가져옴

            OAuthAttributes attributes = OAuthAttributes.ofKakao(oAuth2User.getAttributes()); //회원정보 JSON 정제해서 반환

            Map<String, Object> newAttribute = updateAttributes(attributes);
            User user = saveOrUpdate(attributes);
            String key = user.getRole();


            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(key)),
                    newAttribute, "id");

        }catch(OAuth2AuthenticationException e) {
            throw new RuntimeException();
        }
    }

    //첫번째 로그인인지 확인
    private Map<String, Object> updateAttributes(OAuthAttributes attributes) {
        User user = userRepository.findMemberByKakaoId(attributes.getKakaoId())
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
        Map<String, Object> newAttribute = new HashMap<String, Object>();
        newAttribute.putAll(attributes.getAttributes());

        if(user==null) {
            newAttribute.put("firstLogin", true);
        }
        else {
            newAttribute.put("firstLogin", false);
        }
        return newAttribute;
    }

    //인증된 유저 DTO 반환
    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findMemberByKakaoId(attributes.getKakaoId())
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다.")); //db에 있는 유저인지 확인
            return user; //유저반환

    }

//    @Transactional
//    public String deleteSessionMember(Long memberId) {
//        User user = findMemberEntity(memberId);
//        user.updateDeleteFlag();
//        return "삭제 완료";
//    }
//
//    public User findMemberEntity(Long memberId) {
//        return userRepository.findMemberByMemberIdAndDeleteFlagIsFalse(memberId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, null));
//    }


}

