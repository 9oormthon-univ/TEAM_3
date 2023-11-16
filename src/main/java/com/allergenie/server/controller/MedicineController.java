package com.allergenie.server.controller;

import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MedicineListDto;
import com.allergenie.server.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping
    public MedicineListDto getMedicineListBySearch(
            @RequestParam(name = "search") String search,
            @RequestParam(name = "pageNo") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 4);
        return medicineService.getMedicineListBySearch(search, pageable);
    }

    @GetMapping("/{medicineId}")
    public MedicineInfoDto getMedecineInfo(@PathVariable Long medicineId) {
        return medicineService.getMedicineInfo(medicineId);
    }
}
