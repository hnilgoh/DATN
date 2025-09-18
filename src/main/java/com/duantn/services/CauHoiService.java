package com.duantn.services;

import java.util.List;

import com.duantn.entities.CauHoi;

public interface CauHoiService {
    List<CauHoi> findByBaiTracNghiemId(Integer tracnghiemId);

    CauHoi save(CauHoi cauHoi);

    void deleteById(Integer id);

    CauHoi getReferenceById(Integer id);

    CauHoi saveAndFlush(CauHoi cauHoi);

    CauHoi findById(Integer id);

}
