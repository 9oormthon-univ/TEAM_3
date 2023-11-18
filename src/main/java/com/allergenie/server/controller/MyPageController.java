package com.allergenie.server.controller;

import com.allergenie.server.config.jwt.JwtTokenProvider;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MyPageDto;
import com.allergenie.server.dto.response.ProhibitionInfoDto;
import com.allergenie.server.service.MyPageService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public MyPageDto getMyPage(HttpServletRequest request) {
        User user = jwtTokenProvider.getUserInfoByToken(request);
        return myPageService.getMyPage(user);
    }

    //약 정보 나타내기
    @GetMapping("/{medicineId}")
    public ProhibitionInfoDto getProhibitionInfo(@PathVariable Long medicineId) {
        return myPageService.getProhibitionInfo(medicineId);
    }

    //약 정보 추가하기
    @PostMapping("/{medicineId}")
    public ResponseEntity<Void> addProhibitionInfo(@PathVariable Long medicineId, HttpServletRequest request) {
        User user = jwtTokenProvider.getUserInfoByToken(request);
        return myPageService.addProhibitionInfo(medicineId, user);
    }
}
