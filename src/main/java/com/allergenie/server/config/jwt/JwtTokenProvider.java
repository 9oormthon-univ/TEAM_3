package com.allergenie.server.config.jwt;

import com.allergenie.server.domain.User;
import com.allergenie.server.repository.UserRepository;
import com.allergenie.server.service.RedisService;
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

    private final RedisService redisService;
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
        redisService.setValues(userId, refreshToken, Duration.ofMillis(tokenInvalidTime));
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

    // 토큰의 유효성 + 만료일자 확인
//    public Authentication validateToken(HttpServletRequest request, String token) {
//        System.out.println("토큰 검증 첫번째");
//        String exception = "exception";
//        try {
//            //만료된 액세스 토큰을 확인하기 위함
//            String expiredAT = redisService.getValues(blackListATPrefix + token);
//            System.out.println("레디스안에");
//            if (expiredAT != null) { // redis에 해당 토큰이 저장되어 있으면 만료되었다는 뜻
//                throw new ExpiredJwtException(null, null, null);
//            }
//            // 토큰을 분석하고 서명을 확인하는 절차
//            // 토큰의 유효성을 확인하는 중요한 절차
//            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
//            // 유효한 토큰인 경우, 함수를 호출하여 사용자 인증 정보를 얻고 반환한다.
//            return getAuthentication(token);
//
//        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException e) {
//            request.setAttribute(exception, "토큰의 형식을 확인하세요.");
//        } catch (ExpiredJwtException e) {
//            request.setAttribute(exception, "access 토큰이 만료되었습니다.");
//        } catch (IllegalArgumentException e) {
//            request.setAttribute(exception, "JWT compact of handler are invalid");
//        }
//        return null;
//    }


    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


//    public Authentication getAuthentication(String token) {
//            System.out.println("authentication");
//            UserDetails userDetails = customOAuthUserService.loadUserByUsername(getUserPk(token));
//            System.out.println(userDetails+"getAuthentication");
//            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//
//    }


//    private boolean userDetailsExists(String token) {
//        try {
//            UserDetails userDetails = customOAuthUserService.loadUserByUsername(getUserPk(token));
//            return userDetails != null; // 정보가 존재하는 경우 true를 반환
//        } catch (UsernameNotFoundException ex) {
//            return false;
//        }
//    }


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
        redisService.setValues(blackListATPrefix + accessToken, userId, Duration.ofMillis(expiredAccessTokenTime));
        redisService.deleteValues(userId);
    }


    //HTTP 요청 안에서 헤더 찾아서 토큰 가져옴
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }


    // 토큰을 사용해서 사용자 엔티티 정보를 가져와서 반환하기
    public User getUserInfoByToken(HttpServletRequest request) {
        String token = resolveToken(request);
        boolean isauthentication = validateToken(token);
        System.out.println(isauthentication+"^^^^^");
        String email = getUserPk(token);
        if (isauthentication) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("회원이 존재하지 않습니다."));
            System.out.println(user.getEmail()+"^^^^");
            return user;
        }
        else {
            return null;
        }
    }


    // 클라이언트의 refreshToken 과 서버에 저장된 refreshToken이 같은지 확인한다
//    public void checkRefreshToken(String userId, String refreshToken) {
//        String redisRT = redisService.getValues(userId);
//        if (!refreshToken.equals(redisRT)) { // 일치하지 않는 경우 에러 발생
//            throw new RefreshTokenExpiredException();
//        }
//    }
}
