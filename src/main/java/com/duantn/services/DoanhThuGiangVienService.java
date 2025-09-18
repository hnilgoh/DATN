package com.duantn.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.duantn.entities.DoanhThuGiangVien;

public interface DoanhThuGiangVienService {
    BigDecimal tinhDoanhThuTheoKhoangNgay(Integer taiKhoanId, LocalDateTime startDate, LocalDateTime endDate);

    List<DoanhThuGiangVien> findByTaiKhoanGV_Id(Integer taikhoanId);

}
