package com.allergenie.server.dto.response;

import com.allergenie.server.domain.Medicine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MedicineDto {
    private Long medicineId;
    private String name;

    @Builder
    public MedicineDto(Medicine medicine) {
        this.medicineId = medicine.getId();
        this.name = medicine.getName();

    }
}