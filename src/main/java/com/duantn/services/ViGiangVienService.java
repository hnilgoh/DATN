package com.duantn.services;

import com.duantn.entities.DoanhThuGiangVien;
import com.duantn.entities.RutTienGiangVien;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiRutTien;

import java.math.BigDecimal;
import java.util.List;

public interface ViGiangVienService {
    BigDecimal tinhSoDu(TaiKhoan giangVien);

    List<DoanhThuGiangVien> getLichSuThuNhap(TaiKhoan giangVien);

    List<RutTienGiangVien> getLichSuRutTienThanhCong(TaiKhoan giangVien);

    List<RutTienGiangVien> getYeuCauDangXuLy(TaiKhoan giangVien);

    boolean guiYeuCauRutTien(TaiKhoan giangVien, BigDecimal soTienRut);

    List<RutTienGiangVien> findRutTienTheoTrangThai(TaiKhoan giangVien, List<TrangThaiRutTien> trangThai);

    RutTienGiangVien getLastRutTien(TaiKhoan giangVien);

    boolean guiYeuCauRutTienFull(TaiKhoan giangVien, BigDecimal soTienRut, String soTaiKhoan, String tenNganHang);

    //
    BigDecimal getTongThuTrongThang(TaiKhoan giangVien);

    Long getSoLanNhanTrongThang(TaiKhoan giangVien);
}