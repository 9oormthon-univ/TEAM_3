package com.allergenie.server.service;

import com.allergenie.server.domain.Medicine;
import com.allergenie.server.domain.Prohibition;
import com.allergenie.server.domain.User;
import com.allergenie.server.dto.response.MedicineInfoDto;
import com.allergenie.server.dto.response.MyPageDto;
import com.allergenie.server.dto.response.ProhibitionDto;
import com.allergenie.server.repository.MedicineRepository;
import com.allergenie.server.repository.ProhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPageService {
    private final ProhibitionRepository prohibitionRepository;
    private final MedicineRepository medicineRepository;

    public MyPageDto getMyPage(User user) {
        List<Prohibition> prohibitionList = prohibitionRepository.findByUser(user);
        List<ProhibitionDto> prohibitionDtos = prohibitionList.stream()
                .map(prohibition -> new ProhibitionDto(prohibition.getMedicine()))
                .toList();
        return new MyPageDto(user, prohibitionDtos);
    }

    public MedicineInfoDto getProhibitionInfo(Long medicineId) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "약 정보를 찾을 수 없습니다"));
        return new MedicineInfoDto(medicine);
    }
}
