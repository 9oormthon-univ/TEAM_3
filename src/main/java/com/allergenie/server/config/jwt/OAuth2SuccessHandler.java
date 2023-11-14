package com.allergenie.server.config.jwt;

import com.allergenie.server.domain.User;
import com.allergenie.server.repository.UserRepository;
import com.allergenie.server.service.OAuthUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private OAuthUserService oAuthUserService;

//    @Value("${app.deployment.url}")
//    private String requestUrl;
//
//    @Value("${app.deployment.processor.url}")
//    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        User user = userRepository.findMemberByKakaoId(oAuth2User.getAttribute("id"))
                .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다.")); // 해당 id를 디비에서 조회
        String role = user.getRole();
        Boolean firstLogin = oAuth2User.getAttribute("firstLogin");

        log.info("OAuth2User = {}", oAuth2User);
        String targetUrl;
        log.info("토큰 발행 시작");

        String token = jwtTokenProvider.createOauthAccessToken(oAuth2User.getAttribute("id").toString(), role); //토큰발행
        log.info("{}", token);

        targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/kakaologin")
                .queryParam("token", token)
                .queryParam("firstLogin", firstLogin)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

