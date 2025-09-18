package com.duantn.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import org.springframework.transaction.annotation.Transactional;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.repositories.DangHocRepository;
import com.duantn.repositories.DoanhThuGiangVienRepository;
import com.duantn.repositories.GiangVienRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.GiangVienService;
import com.duantn.dtos.DoanhThuKhoaHocGiangVienDto;
import com.duantn.dtos.HocVienDto;
import com.duantn.dtos.HocVienTheoKhoaHocDto;
import com.duantn.dtos.KhoaHocDiemDto;

@Service
public class GiangVienServiceImpl implements GiangVienService {
    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private DangHocRepository dangHocRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private DoanhThuGiangVienRepository doanhThuGiangVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public List<GiangVien> timKiemTheoTenIgnoreCase(String ten) {
        if (ten == null || ten.trim().isEmpty()) {
            return giangVienRepository.findAll();
        }
        return giangVienRepository.findByTenGiangVienContainingIgnoreCaseNative(ten.trim());
    }

    @Override
    public List<DoanhThuKhoaHocGiangVienDto> thongKeDoanhThuTheoGiangVien(Integer giangVienId) {
        return giangVienRepository.thongKeDoanhThuTheoGiangVien(giangVienId);
    }

    @Override
    public GiangVien findByTaikhoan(TaiKhoan taiKhoan) {
        return giangVienRepository.findByTaikhoan(taiKhoan).orElse(null);
    }

    @Transactional
    @Override
    public double tinhDiemDanhGiaTrungBinh(Integer giangVienId) {
        Double diemTB = giangVienRepository.tinhDiemDanhGiaTrungBinh(giangVienId);
        return diemTB != null ? diemTB : 0.0;
    }

    @Override
    public List<HocVienTheoKhoaHocDto> thongKeHocVienTheoKhoaHoc(Integer giangVienId) {
        return dangHocRepository.demSoHocVienTheoKhoaHoc(giangVienId);
    }

    @Override
    public List<KhoaHoc> timTatCaKhoaHocDangDay(Integer giangVienId) {
        return khoaHocRepository.findByGiangVien_GiangvienIdAndTrangThai(
                giangVienId, TrangThaiKhoaHoc.PUBLISHED);
    }

    @Override
    public List<KhoaHocDiemDto> layDiemTrungBinhCacKhoaHocXuatBan(Integer giangVienId) {
        return khoaHocRepository.findDiemTrungBinhTheoKhoaHocXuatBan(giangVienId);
    }

    @Override
    public double layTongTienNhan(Integer giangVienId) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByGiangVien_GiangvienId(giangVienId);
        if (taiKhoan == null) {
            return 0;
        }

        BigDecimal tongTien = doanhThuGiangVienRepository.tongTienNhanTheoGiangVien(taiKhoan.getTaikhoanId());
        return tongTien != null ? tongTien.doubleValue() : 0;
    }

    //
    @Override
    public long demHocVienTheoGiangVien(Integer giangVienId) {
        return dangHocRepository.demSoHocVienTheoGiangVien(giangVienId);
    }

    @Override
    public GiangVien getByTaiKhoanId(Integer taiKhoanId) {
        return giangVienRepository.findByTaikhoan_TaikhoanId(taiKhoanId).orElse(null);
    }

    @Override
    public boolean capNhatThongTinNganHang(Integer giangVienId, String soTaiKhoan, String tenNganHang) {
        Optional<GiangVien> optionalGV = giangVienRepository.findById(giangVienId);
        if (optionalGV.isPresent()) {
            GiangVien gv = optionalGV.get();
            giangVienRepository.save(gv);
            return true;
        }
        return false;
    }

    @Override
    public GiangVien getByTaiKhoan(TaiKhoan taiKhoan) {
        return giangVienRepository.getByTaikhoan(taiKhoan);
    }

    @Override
    public GiangVien getById(Integer id) {
        return giangVienRepository.findById(id).orElse(null);
    }

    @Override
    public GiangVien findByTaiKhoan(TaiKhoan taiKhoan) {
        return giangVienRepository.findByTaikhoan(taiKhoan).orElse(null);
    }

    @Override
    public GiangVien findById(Integer id) {
        return giangVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên với ID: " + id));
    }

    @Override
    public List<HocVienDto> findHocVienTheoKhoaHoc(Integer khoaHocId) {
        return dangHocRepository.findHocVienTheoKhoaHoc(khoaHocId);
    }
}
