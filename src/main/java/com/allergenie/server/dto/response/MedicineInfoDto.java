package com.allergenie.server.dto.response;

import com.allergenie.server.domain.Medicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MedicineInfoDto {
    private Long medicineId;
    private String image;
    private String name;
    private String effect;
//    private String sideEffect;
    private String caution;

    @Builder
    public MedicineInfoDto(Medicine medicine) {
        this.medicineId = medicine.getId();
        this.image = medicine.getImage();
        this.name = medicine.getName();
        this.effect = medicine.getEffect();
//        this.sideEffect
        this.caution = medicine.getCaution();
    }
}
