package com.duantn.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.duantn.dtos.KetQuaRequestDto;
import com.duantn.entities.KetQua;

public interface KetQuaService {
    KetQua luuKetQua(KetQuaRequestDto dto);

    List<Map<String, Object>> layChiTietTheoKetQuaId(Integer ketQuaId);

    Optional<KetQua> findByTaiKhoanIdAndTracNghiemId(Integer taiKhoanId, Integer tracnghiemId);

    void xoaChiTietTheoKetQuaId(Integer ketQuaId);

}
