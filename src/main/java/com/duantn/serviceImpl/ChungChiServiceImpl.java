package com.duantn.serviceImpl;

import com.duantn.entities.ChungChi;
import com.duantn.repositories.ChungChiRepository;
import com.duantn.services.ChungChiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChungChiServiceImpl implements ChungChiService {

    @Autowired
    private ChungChiRepository chungChiRepository;

    @Override
    public ChungChi layTheoDangHocId(Integer danghocId) {
        return chungChiRepository.findByDanghoc_DanghocId(danghocId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chứng chỉ với danghocId: " + danghocId));
    }

}
