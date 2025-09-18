package com.duantn.services;

import java.util.List;
import java.util.Optional;

import com.duantn.entities.DanhMuc;

public interface DanhMucService {
    List<DanhMuc> layTatCa();

    DanhMuc layTheoId(Integer id);

    DanhMuc taoDanhMuc(DanhMuc danhMuc);

    DanhMuc capNhat(Integer id, DanhMuc danhMuc);

    void voHieuHoa(Integer id);

    boolean daTonTaiTen(String tenDanhMuc);

    boolean daTonTaiTenKhacId(String tenDanhMuc, Integer idHienTai);

    void kichHoat(Integer id);

    Optional<DanhMuc> findBySlug(String slug);
}