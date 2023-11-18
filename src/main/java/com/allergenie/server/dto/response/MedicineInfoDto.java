package com.allergenie.server.dto.response;

import com.allergenie.server.domain.Medicine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MedicineInfoDto {
    private String userImageURL;
    private Long medicineId;
    private String image;
    private String name;
    private String effect;
    private String caution;

    @Builder
    public MedicineInfoDto(String userImageURL, Medicine medicine) {
        this.userImageURL = userImageURL;
        this.medicineId = medicine.getMedicineId();
        this.image = medicine.getImage();
        this.name = medicine.getName();
        this.effect = medicine.getEffect();
        this.caution = medicine.getCaution();
    }
}
