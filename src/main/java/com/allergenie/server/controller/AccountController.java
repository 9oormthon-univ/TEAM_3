package com.allergenie.server.controller;

import com.allergenie.server.dto.request.UserFormDto;
import com.allergenie.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping(value = "/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    //일반 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserFormDto userFormDto) {
        userService.signup(userFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");

    }
}
