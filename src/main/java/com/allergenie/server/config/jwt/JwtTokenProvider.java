package com.allergenie.server.config.jwt;

import com.allergenie.server.domain.User;
import com.allergenie.server.repository.UserRepository;
//import com.allergenie.server.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final UserRepository userRepository;

    //private final RedisService redisService;
    @Value("${spring.jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${spring.jwt.blacklist.access-token}")
    private String blackListATPrefix;

    private final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 120; // 2h

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    public String createOauthAccessToken(String userPk, String roles) {
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);  // 권한 설정, key/ value 쌍으로 저장
        Date now = new Date(); // 현재 시간 -> 유효기간 확인을 위함
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String createAccessToken(String userId, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 120; // 2h
        return this.createToken(userId, roles, tokenInvalidTime);
    }


    public String createRefreshToken(String userId, String roles) {
        Long tokenInvalidTime = 1000L * 60 * 60 * 24; // 1d
        String refreshToken = this.createToken(userId, roles, tokenInvalidTime);
        //refresh token은 redis에 저장
        //redisService.setValues(userId, refreshToken, Duration.ofMillis(tokenInvalidTime));
        return refreshToken;
    }

    public String createToken(String userId, String roles, Long tokenInvalidTime) {
        //user 구분을 위해 user pk 값 넣어줌
        Claims claims = Jwts.claims().setSubject(userId); // claims 생성 및 payload 설정
        claims.put("roles", roles); // 권한 설정, key/ value 쌍으로 저장
        Date date = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + tokenInvalidTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }



    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }



    //토큰에서 사용자의 기본키(이메일) 찾기
    public String getUserPk(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    // access 토큰 만료시간을 체크 후, redis에 (blacklist) + accessToken, 계정, 만료기간을이 담긴
    // ValueOperation을 만들어 redis에 저장한다.
    // redis에서 유저 refreshtoken 값을 삭제한다
    public void logout(String userId, String accessToken) {
        Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(accessToken).getBody().getExpiration();
        long expiredAccessTokenTime = expiration.getTime() - new Date().getTime();
        //redisService.setValues(blackListATPrefix + accessToken, userId, Duration.ofMillis(expiredAccessTokenTime));
        //redisService.deleteValues(userId);
    }


    //HTTP 요청 안에서 헤더 찾아서 토큰 가져옴
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }


    // 토큰을 사용해서 사용자 엔티티 정보를 가져와서 반환하기
    public User getUserInfoByToken(HttpServletRequest request) {
        String token = resolveToken(request);
        boolean isauthentication = validateToken(token);
        String email = getUserPk(token);
        if (isauthentication) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
            return user;
        }
        else {
            return null;
        }
    }


    // 클라이언트의 refreshToken 과 서버에 저장된 refreshToken이 같은지 확인한다
    public void checkRefreshToken(String userId, String refreshToken) {
        //String redisRT = redisService.getValues(userId);
//        if (!refreshToken.equals(redisRT)) { // 일치하지 않는 경우 에러 발생
//            throw new RuntimeException(); // 추후 수정
//        }
    }
}
