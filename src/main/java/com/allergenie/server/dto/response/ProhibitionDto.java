package com.allergenie.server.dto.response;

import com.allergenie.server.domain.Medicine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProhibitionDto {
    private Long medicineId;
    private String name;

    @Builder
    public ProhibitionDto(Medicine medicine) {
        this.medicineId = medicine.getMedicineId();
        this.name = medicine.getName();
    }
}
