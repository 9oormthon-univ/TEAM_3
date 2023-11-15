package com.allergenie.server.controller;

import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MedicineListDto;
import com.allergenie.server.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping
    public MedicineListDto getMedicineListBySearch(@RequestParam("search") String search) {
        return medicineService.getMedicineListBySearch(search);
    }

    @GetMapping("/{medicineId}")
    public MedicineInfoDto getMedecineInfo(@PathVariable Long medicineId) {
        return medicineService.getMedicineInfo(medicineId);
    }
}
