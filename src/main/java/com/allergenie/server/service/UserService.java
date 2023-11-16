package com.allergenie.server.service;

import com.allergenie.server.config.jwt.JwtTokenProvider;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.request.UserFormDto;
import com.allergenie.server.dto.response.LoginInfoDto;
import com.allergenie.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    //private final RedisService redisService;

    //회원가입
    @Transactional
    public User signup(UserFormDto userFormDto){
        //회원정보폼에서 비밀번호를 가져온 후 -> 인코딩해서 다시 userFormDto에 넣음
        userFormDto.setPassword(passwordEncoder.encode(userFormDto.getPassword()));
        //회원정보폼에서 이메일을 가져온 후 -> 해당 유저 객체를 불러온다
        Optional<User> existingUser = userRepository.findByEmail(userFormDto.getEmail());
        if( existingUser.isPresent()){
            //throw new DuplicateEmailException();
        }
        User user = userRepository.save(userFormDto.toEntity());
        return user;
    }

    @Transactional
    public LoginInfoDto login(String email, String password){
        User user =userRepository.findByEmail(email).orElseThrow(RuntimeException::new);//추후수정
        checkPassword(password, user.getPassword());
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(),user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole());
        return new LoginInfoDto(accessToken, refreshToken, user.getNickname());
    }

    //DB에 있는 비밀번호와 사용자로부터 받은 비밀번호의 일치여부 확인..
    private void checkPassword(String password, String encodedPassword) {
        boolean isSame = passwordEncoder.matches(password, encodedPassword);
        if (!isSame) {
            //throw new PasswordNotMatchedException();
        }

    }

    public void logout(HttpServletRequest request, String email, String accessToken){
        // 로그아웃 하고 싶은 토큰이 유효한 지 먼저 검증하기
        boolean authentication = jwtTokenProvider.validateToken(accessToken);
        if (!authentication){
            System.out.println(accessToken);
            throw new IllegalArgumentException("로그아웃 : 유효하지 않은 토큰입니다.");
        }
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new); //추후수정
        // 레디스에서 해당 유저의 정보를 삭제
        //redisService.deleteValues(user.getEmail()); -> jwtTokenProvider 에서 이미 함
        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        jwtTokenProvider.logout(email, accessToken);
    }

    public ResponseCookie generateCookie(String type, String token)
    {
        ResponseCookie cookie = ResponseCookie.from(type, token)
                .maxAge(7 * 24 * 60 * 60) //쿠키의 수명=7일
                .path("/") // 쿠키의 경로를 설정함. 이경우 '/'로 되어있는 모든 경로에서 쿠키에 접근 가능
                .secure(true) // HTTPS에서만 접근 가능
                .sameSite("None")
                .httpOnly(true) // 서버측에서만 쿠키 접근 가능
                .build();
        return cookie;

    }

    //전달받은 유저의 이메일로 유저가 존재하는지 확인 + refreshtokne이 유효한지 체크
    //accessToken 재생성하여 refreshToken과 함께 응답
    public LoginInfoDto reIssueAccessToken(String email, String refreshToken) {
        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);//추후수정
        jwtTokenProvider.checkRefreshToken(email, refreshToken);
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        return new LoginInfoDto(accessToken, refreshToken, user.getNickname());
    }

}
