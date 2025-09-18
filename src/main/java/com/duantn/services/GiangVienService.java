package com.duantn.services;

import java.util.List;

import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.dtos.DoanhThuKhoaHocGiangVienDto;
import com.duantn.dtos.HocVienDto;
import com.duantn.dtos.HocVienTheoKhoaHocDto;
import com.duantn.dtos.KhoaHocDiemDto;

public interface GiangVienService {
    List<GiangVien> timKiemTheoTenIgnoreCase(String ten);

    List<DoanhThuKhoaHocGiangVienDto> thongKeDoanhThuTheoGiangVien(Integer giangVienId);

    GiangVien findByTaikhoan(TaiKhoan taiKhoan);

    GiangVien findByTaiKhoan(TaiKhoan taiKhoan);

    double tinhDiemDanhGiaTrungBinh(Integer giangVienId);

    //
    List<HocVienTheoKhoaHocDto> thongKeHocVienTheoKhoaHoc(Integer giangVienId);

    List<KhoaHoc> timTatCaKhoaHocDangDay(Integer giangVienId);

    List<KhoaHocDiemDto> layDiemTrungBinhCacKhoaHocXuatBan(Integer giangVienId);

    double layTongTienNhan(Integer giangVienId);

    //
    long demHocVienTheoGiangVien(Integer giangVienId);

    //
    GiangVien getByTaiKhoanId(Integer taiKhoanId);

    boolean capNhatThongTinNganHang(Integer giangVienId, String soTaiKhoan, String tenNganHang);

    GiangVien getByTaiKhoan(TaiKhoan taiKhoan);

    GiangVien getById(Integer id);

    GiangVien findById(Integer id);

    List<HocVienDto> findHocVienTheoKhoaHoc(Integer khoaHocId);

}
