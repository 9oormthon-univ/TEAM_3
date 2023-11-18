package com.allergenie.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MedicineListDto {
    private String userImageURL;
    private int totalPage;
    private int currentPage;
    private List<MedicineDto> medicineList;

    @Builder
    public MedicineListDto(int totalPage, int currentPage, String userImageURL, List<MedicineDto> medicineList) {
        this.userImageURL = userImageURL;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.medicineList = medicineList;
    }
}
