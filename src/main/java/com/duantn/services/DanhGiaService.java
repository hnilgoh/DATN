package com.duantn.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;

@Service
public interface DanhGiaService {
    List<DanhGia> findByKhoaHocId(Integer khoaHocId);

    boolean daDanhGia(Integer khoaHocId, Integer taiKhoanId);

    void taoDanhGia(Integer khoaHocId, Integer taiKhoanId, Integer diem, String noiDung);

    Double tinhTrungBinhDanhGia(Integer khoaHocId);

    void taoHoacCapNhatDanhGia(Integer khoaHocId, TaiKhoan nguoiDung, DanhGia danhGiaMoi);

    Optional<DanhGia> findByTaikhoanAndKhoahoc(TaiKhoan taikhoan, KhoaHoc khoahoc);

    void xoaDanhGia(KhoaHoc khoaHoc, TaiKhoan taiKhoan);

    //
    long demSoLuongDanhGia(Integer khoaHocId);

    Double diemTrungBinh(Integer khoaHocId);

    List<DanhGia> findByDanhGia(Integer khoaHocId);

    List<DanhGia> findByGiangVienId(Integer giangvienId);

    Optional<DanhGia> findById(Integer id);

    Integer xoaDanhGiaTheoId(Integer danhGiaId, TaiKhoan nguoiDung);
}
