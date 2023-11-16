package com.allergenie.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MedicineListDto {
    private int totalPage;
    private int currentPage;
    private List<MedicineDto> medicineList;

    @Builder
    public MedicineListDto(int totalPage, int currentPage, List<MedicineDto> medicineList) {
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.medicineList = medicineList;
    }
}
