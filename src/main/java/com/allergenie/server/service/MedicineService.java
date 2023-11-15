package com.allergenie.server.service;

import com.allergenie.server.domain.Medicine;
import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MedicineListDto;
import com.allergenie.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineService {
    private final MedicineRepository medicineRepository;

    public MedicineListDto getMedicineListBySearch(String search) {

    }

    public MedicineInfoDto getMedicineInfo(Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new MedicineInfoDto(medicine);
    }

}
