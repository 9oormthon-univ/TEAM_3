package com.allergenie.server.service;

import com.allergenie.server.domain.Medicine;
import com.allergenie.server.dto.request.MedicineReq;
import com.allergenie.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@org.springframework.transaction.annotation.Transactional
@RequiredArgsConstructor
public class MyPageService {

    private final MedicineRepository medicineRepository;

//    @Transactional
//    public Long deleteScrap(HttpServletRequest httpRequest, CopyReq copyReq) {
//        User user = jwtTokenProvider.getUserInfoByToken(httpRequest);
//        Long cardId = copyReq.getCardId();
//        Card card = cardRepository.findCardById(cardId);
//
//        Scrap scrap = scrapRepository.findByUserAndCard(user, card);
//
//        if (scrap != null) {
//            scrap.setDeleteYn(1);
//            return cardId;
//        }
//
//        return null;
//    }
//
//    @Transactional
//    public Long deleteMedicine(Long userId, MedicineReq medicineReq){
//        Medicine medicine = medicineRepository.findByMedicineId(medicineReq.getMedicineId());
//    }

}
