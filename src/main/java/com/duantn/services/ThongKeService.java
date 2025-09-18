package com.duantn.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiKhoaHoc;

public interface ThongKeService {
    int tongHocVien();

    int tongGiangVien();

    int countHocVienDaDangKy();

    int countKhoaHocByTrangThai(TrangThaiKhoaHoc trangThai);

    double doanhThuThangNay();

    List<Double> getDoanhThu6Thang();

    Map<String, Integer> getTiLeDanhMuc();

    List<Object> getChiTietKhoaHoc();

    double tongTienNenTang();

    int tongNhanVien();

    List<String> getTopKhoaHocLabels();

    List<Long> getTopKhoaHocSoLuong();

    Map<String, Long> thongKeTaiKhoanTheoVaiTro();

    Map<String, BigDecimal> getDoanhThuTheo6ThangGanNhat();

    List<Object[]> getTop5DanhMuc();

    List<KhoaHoc> getAllKhoaHocDaXuatBan();

    List<Object[]> getTop3GiangVienDoanhThu();

    List<Object[]> getTop5GiangVienHocVien();

    Map<Integer, Double> layDoanhThuTheoThang(TaiKhoan giangVien);
}