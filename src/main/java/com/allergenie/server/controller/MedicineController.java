package com.allergenie.server.controller;

import com.allergenie.server.config.jwt.JwtTokenProvider;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MedicineListDto;
import com.allergenie.server.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class MedicineController {
    private final MedicineService medicineService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public MedicineListDto getMedicineListBySearch(
            @RequestParam(name = "search") String search,
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            HttpServletRequest request) {
        User user = jwtTokenProvider.getUserInfoByToken(request);
        Pageable pageable = PageRequest.of(pageNo, 3);
        return medicineService.getMedicineListBySearch(user, search, pageable);
    }

    @GetMapping("/{medicineId}")
    public MedicineInfoDto getMedecineInfo(@PathVariable Long medicineId, HttpServletRequest request) {
        User user = jwtTokenProvider.getUserInfoByToken(request);
        return medicineService.getMedicineInfo(user, medicineId);
    }
}
