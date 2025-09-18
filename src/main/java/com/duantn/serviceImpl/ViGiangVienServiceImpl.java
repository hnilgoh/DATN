package com.duantn.serviceImpl;

import com.duantn.entities.DoanhThuGiangVien;
import com.duantn.entities.RutTienGiangVien;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiRutTien;
import com.duantn.repositories.DoanhThuGiangVienRepository;
import com.duantn.repositories.RutTienGiangVienRepository;
import com.duantn.services.ViGiangVienService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class ViGiangVienServiceImpl implements ViGiangVienService {

    @Autowired
    private DoanhThuGiangVienRepository doanhThuRepo;

    @Autowired
    private RutTienGiangVienRepository rutTienRepo;

    @Override
    public BigDecimal tinhSoDu(TaiKhoan giangVien) {
        BigDecimal tongThuNhap = doanhThuRepo.findByTaikhoanGV(giangVien).stream()
                .map(DoanhThuGiangVien::getSotiennhan)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TrangThaiRutTien> trangThaiCanTru = Arrays.asList(
                TrangThaiRutTien.THANH_CONG,
                TrangThaiRutTien.DANG_CHO_XU_LY);

        BigDecimal tongDaRut = rutTienRepo.findByTaikhoanGVAndTrangthaiIn(giangVien, trangThaiCanTru).stream()
                .map(RutTienGiangVien::getSoTienRut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return tongThuNhap.subtract(tongDaRut);
    }

    @Override
    public List<DoanhThuGiangVien> getLichSuThuNhap(TaiKhoan giangVien) {
        return doanhThuRepo.findByTaikhoanGV(giangVien);
    }

    @Override
    public List<RutTienGiangVien> getLichSuRutTienThanhCong(TaiKhoan giangVien) {
        return rutTienRepo.findByTaikhoanGVAndTrangthai(giangVien, TrangThaiRutTien.THANH_CONG);
    }

    @Override
    public List<RutTienGiangVien> getYeuCauDangXuLy(TaiKhoan giangVien) {
        return rutTienRepo.findByTaikhoanGVAndTrangthai(giangVien, TrangThaiRutTien.DANG_CHO_XU_LY);
    }

    @Override
    public boolean guiYeuCauRutTien(TaiKhoan giangVien, BigDecimal soTienRut) {
        BigDecimal soDu = tinhSoDu(giangVien);

        if (soTienRut.compareTo(BigDecimal.valueOf(100000)) < 0 || soTienRut.compareTo(soDu) > 0) {
            return false;
        }

        RutTienGiangVien rutTien = RutTienGiangVien.builder()
                .taikhoanGV(giangVien)
                .soTienRut(soTienRut)
                .tenGiangVien(giangVien.getName())
                .trangthai(TrangThaiRutTien.DANG_CHO_XU_LY)
                .build();

        rutTienRepo.save(rutTien);
        return true;
    }

    @Override
    public RutTienGiangVien getLastRutTien(TaiKhoan giangVien) {
        return rutTienRepo.findTopByTaikhoanGVOrderByNgayrutDesc(giangVien).orElse(null);
    }

    @Override
    public boolean guiYeuCauRutTienFull(TaiKhoan giangVien, BigDecimal soTienRut, String soTaiKhoan,
            String tenNganHang) {
        BigDecimal soDu = tinhSoDu(giangVien);
        if (soTienRut.compareTo(BigDecimal.valueOf(100000)) < 0 || soTienRut.compareTo(soDu) > 0) {
            return false;
        }
        RutTienGiangVien rutTien = RutTienGiangVien.builder()
                .taikhoanGV(giangVien)
                .soTienRut(soTienRut)
                .tenGiangVien(giangVien.getName())
                .soTaiKhoan(soTaiKhoan)
                .tenNganHang(tenNganHang)
                .trangthai(TrangThaiRutTien.DANG_CHO_XU_LY)
                .build();
        rutTienRepo.save(rutTien);
        return true;
    }

    @Override
    public List<RutTienGiangVien> findRutTienTheoTrangThai(TaiKhoan giangVien, List<TrangThaiRutTien> trangThai) {
        return rutTienRepo.findByTaikhoanGVAndTrangthaiIn(giangVien, trangThai);
    }

    //
    @Override
    public BigDecimal getTongThuTrongThang(TaiKhoan giangVien) {
        return doanhThuRepo.getTongThuTrongThang(giangVien);
    }

    @Override
    public Long getSoLanNhanTrongThang(TaiKhoan giangVien) {
        return doanhThuRepo.getSoLanNhanTrongThang(giangVien);
    }
}