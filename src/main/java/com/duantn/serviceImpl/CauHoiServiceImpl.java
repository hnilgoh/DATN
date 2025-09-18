package com.duantn.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.CauHoi;
import com.duantn.repositories.CauHoiRepository;
import com.duantn.services.CauHoiService;

@Service
public class CauHoiServiceImpl implements CauHoiService {

    @Autowired
    CauHoiRepository cauHoiRepository;

    @Override
    public List<CauHoi> findByBaiTracNghiemId(Integer tracNghiemId) {
        return cauHoiRepository.findAllByBaiTracNghiem_TracnghiemId(tracNghiemId);
    }

    @Override
    public CauHoi save(CauHoi cauHoi) {
        return cauHoiRepository.save(cauHoi);
    }

    @Override
    public void deleteById(Integer id) {
        cauHoiRepository.deleteById(id);
    }

    @Override
    public CauHoi getReferenceById(Integer id) {
        return cauHoiRepository.getReferenceById(id);
    }

    @Override
    public CauHoi saveAndFlush(CauHoi cauHoi) {
        return cauHoiRepository.saveAndFlush(cauHoi);
    }

    @Override
    public CauHoi findById(Integer id) {
        return cauHoiRepository.findById(id).orElse(null);
    }

}
