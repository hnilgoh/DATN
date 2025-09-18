package com.duantn.services;

import java.util.List;

import com.duantn.entities.DangHoc;
import com.duantn.entities.KhoaHoc;

public interface DangHocService {
    long demSoLuongDangKy(Integer khoaHocId);

    DangHoc findByTaiKhoanIdAndKhoaHocId(Integer taiKhoanId, Integer khoaHocId);

    boolean isEnrolled(Integer taiKhoanId, Integer khoaHocId);

    boolean existsByKhoaHocId(Integer khoaHocId);

    List<KhoaHoc> findKhoaHocByHocVienId(Integer hocVienId);

}
