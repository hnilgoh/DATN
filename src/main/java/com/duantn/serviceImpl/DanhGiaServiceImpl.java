package com.duantn.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.DanhGiaRepository;
import com.duantn.services.DanhGiaService;
import com.duantn.services.KhoaHocService;
import com.duantn.services.TaiKhoanService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DanhGiaServiceImpl implements DanhGiaService {
    private final DanhGiaRepository repo;
    private final KhoaHocService khoaHocService;
    private final TaiKhoanService taiKhoanService;

    @Override
    public List<DanhGia> findByKhoaHocId(Integer khoaHocId) {
        return repo.findByKhoahoc_KhoahocIdOrderByNgayDanhGiaDesc(khoaHocId);
    }

    @Override
    public boolean daDanhGia(Integer khoaHocId, Integer taiKhoanId) {
        return repo.existsByKhoahoc_KhoahocIdAndTaikhoan_TaikhoanId(khoaHocId, taiKhoanId);
    }

    @Override
    public void taoDanhGia(Integer khoaHocId, Integer taiKhoanId, Integer diem, String noiDung) {
        KhoaHoc khoaHoc = khoaHocService.findById(khoaHocId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        TaiKhoan taiKhoan = taiKhoanService.findById(taiKhoanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        DanhGia danhGia = DanhGia.builder()
                .khoahoc(khoaHoc)
                .taikhoan(taiKhoan)
                .diemDanhGia(diem)
                .noiDung(noiDung)
                .build();

        repo.save(danhGia);
    }

    @Override
    public Double tinhTrungBinhDanhGia(Integer khoaHocId) {
        return repo.trungBinhDanhGia(khoaHocId);
    }

    @Override
    public void taoHoacCapNhatDanhGia(Integer khoaHocId, TaiKhoan nguoiDung, DanhGia danhGiaMoi) {
        Optional<DanhGia> optional = repo.findByTaikhoanAndKhoahoc(nguoiDung, danhGiaMoi.getKhoahoc());

        if (optional.isPresent()) {
            DanhGia daCo = optional.get();
            daCo.setDiemDanhGia(danhGiaMoi.getDiemDanhGia());
            daCo.setNoiDung(danhGiaMoi.getNoiDung());
            repo.save(daCo);
        } else {
            danhGiaMoi.setTaikhoan(nguoiDung);
            danhGiaMoi.setKhoahoc(danhGiaMoi.getKhoahoc());
            repo.save(danhGiaMoi);
        }
    }

    @Override
    public Optional<DanhGia> findByTaikhoanAndKhoahoc(TaiKhoan taikhoan, KhoaHoc khoahoc) {
        return repo.findByTaikhoanAndKhoahoc(taikhoan, khoahoc);
    }

    @Override
    public void xoaDanhGia(KhoaHoc khoaHoc, TaiKhoan taiKhoan) {
        Optional<DanhGia> optional = repo.findByTaikhoanAndKhoahoc(taiKhoan, khoaHoc);
        optional.ifPresent(repo::delete);
    }

    //
    @Override
    public long demSoLuongDanhGia(Integer khoaHocId) {
        return repo.countByKhoahoc_KhoahocId(khoaHocId);
    }

    @Override
    public Double diemTrungBinh(Integer khoaHocId) {
        Double diem = repo.tinhDiemTrungBinhTheoKhoaHoc(khoaHocId);
        return diem != null ? Math.round(diem * 10.0) / 10.0 : 0.0; // làm tròn 1 chữ số
    }

    @Override
    public List<DanhGia> findByDanhGia(Integer khoaHocId) {
        return repo.findByKhoaHocId(khoaHocId);
    }

    @Override
    public List<DanhGia> findByGiangVienId(Integer giangvienId) {
        return repo.findByKhoahoc_GiangVien_GiangvienId(giangvienId);
    }

    @Override
    public Optional<DanhGia> findById(Integer id) {
        return repo.findById(id);
    }

    @Override
    public Integer xoaDanhGiaTheoId(Integer danhGiaId, TaiKhoan nguoiDung) {
        DanhGia danhGia = repo.findById(danhGiaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        if (!danhGia.getTaikhoan().getTaikhoanId().equals(nguoiDung.getTaikhoanId())) {
            throw new RuntimeException("Bạn không có quyền xóa đánh giá này");
        }

        Integer khoahocId = danhGia.getKhoahoc().getKhoahocId(); // Lấy ID khóa học trước khi xóa

        repo.delete(danhGia);

        return khoahocId;
    }

}
