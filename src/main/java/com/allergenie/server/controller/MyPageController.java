package com.allergenie.server.controller;

import com.allergenie.server.config.jwt.JwtTokenProvider;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.request.MedicineReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/delete")
    public ResponseEntity<String> deleteMedicine(HttpServletRequest httpRequest, @RequestBody MedicineReq medicineReq) {
        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);


    }

}
