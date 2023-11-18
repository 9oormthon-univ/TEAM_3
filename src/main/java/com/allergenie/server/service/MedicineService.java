package com.allergenie.server.service;

import com.allergenie.server.domain.Medicine;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.response.MedicineDto;
import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MedicineListDto;
import com.allergenie.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicineService {
    private final MedicineRepository medicineRepository;

    public MedicineListDto getMedicineListBySearch(User user, String search, Pageable pageable) {
        Page<Medicine> searchedMedicines = medicineRepository.findByName(search, pageable);
        Page<MedicineDto> medicineDtos = searchedMedicines.map(MedicineDto::new);
        return new MedicineListDto(medicineDtos.getTotalPages(), medicineDtos.getNumber(), user.getImageUrl(), medicineDtos.getContent());
    }

    public MedicineInfoDto getMedicineInfo(User user, Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new MedicineInfoDto(user.getImageUrl(), medicine);
    }

}
