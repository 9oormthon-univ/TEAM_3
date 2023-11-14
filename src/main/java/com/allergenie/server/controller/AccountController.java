package com.allergenie.server.controller;

import com.allergenie.server.config.jwt.JwtTokenProvider;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.request.EmailReqDto;
import com.allergenie.server.dto.request.LoginFormDto;
import com.allergenie.server.dto.request.UserFormDto;
import com.allergenie.server.dto.response.LoginInfoDto;
import com.allergenie.server.dto.response.LoginResDto;
import com.allergenie.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping(value = "/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    //일반 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserFormDto userFormDto) {
        userService.signup(userFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody LoginFormDto loginFormDto){
        LoginInfoDto loginInfoDto = userService.login(loginFormDto.getEmail(), loginFormDto.getPassword());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Set-Cookie",userService.generateCookie("refreshToken", loginInfoDto.getRefreshToken()).toString());
        return new ResponseEntity<LoginResDto>(loginInfoDto.toLoginResDto(), responseHeaders, HttpStatus.OK);

    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        User user = jwtTokenProvider.getUserInfoByToken(request);
        String token = jwtTokenProvider.resolveToken(request);
        userService.logout(request, user.getEmail(), token);
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃이 완료되었습니다.");

    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<LoginResDto> reissueToken(@RequestBody EmailReqDto emailReqDto,
                                                    @CookieValue(value = "refreshToken", required = false) Cookie rCookie){
        String refreshToken = rCookie.getValue();
        System.out.println("refreshToken = " + refreshToken);
        if(refreshToken == null)
        {
            throw new RuntimeException(); // 추후 수정
        }
        LoginInfoDto responseDto = userService.reIssueAccessToken(emailReqDto.getEmail(), refreshToken);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Set-Cookie", userService.generateCookie("refreshToken", responseDto.getRefreshToken()).toString());
        return new ResponseEntity<LoginResDto>(responseDto.toLoginResDto(), responseHeaders, HttpStatus.OK);

    }
}
