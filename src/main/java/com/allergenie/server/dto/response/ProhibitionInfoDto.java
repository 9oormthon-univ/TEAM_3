package com.allergenie.server.dto.response;

import com.allergenie.server.domain.Medicine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProhibitionInfoDto {
    private Long medicineId;
    private String image;
    private String name;
    private String effect;
    private String caution;

    @Builder
    public ProhibitionInfoDto(Medicine medicine) {
        this.medicineId = medicine.getMedicineId();
        this.image = medicine.getImage();
        this.name = medicine.getName();
        this.effect = medicine.getEffect();
        this.caution = medicine.getCaution();
    }
}
