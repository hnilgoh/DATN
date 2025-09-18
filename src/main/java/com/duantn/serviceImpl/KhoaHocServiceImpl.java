package com.duantn.serviceImpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import com.duantn.entities.DanhMuc;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.NguoiDungThichKhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiGiaoDich;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.repositories.DangHocRepository;
import com.duantn.repositories.DanhMucRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.repositories.NguoiDungThichKhoaHocRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.KhoaHocService;

import org.springframework.transaction.annotation.Transactional;

@Service
public class KhoaHocServiceImpl implements KhoaHocService {
    @Autowired
    private KhoaHocRepository khoaHocRepository;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private NguoiDungThichKhoaHocRepository nguoiDungThichKhoaHocRepository;
    @Autowired
    private DangHocRepository dangHocRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Override
    public List<KhoaHoc> getTatCaKhoaHoc() {
        return khoaHocRepository.findAll();
    }

    @Override
    public List<KhoaHoc> getKhoaHocTheoDanhMuc(Integer danhMucId) {
        return khoaHocRepository.findByDanhMuc_DanhmucIdAndTrangThai(danhMucId, TrangThaiKhoaHoc.PUBLISHED);
    }

    @Override
    public KhoaHoc getKhoaHocById(Integer id) {
        return khoaHocRepository.findById(id).orElse(null);
    }

    @Override
    public List<KhoaHoc> layTatCaKhoaHocCanDuyet() {
        return khoaHocRepository.findAllByTrangThai(TrangThaiKhoaHoc.PENDING_APPROVAL);
    }

    @Override
    public List<KhoaHoc> getNewestCourses(int count) {
        Pageable pageable = PageRequest.of(0, count);
        return khoaHocRepository.findByOrderByNgayTaoDesc(pageable);
    }

    @Override
    public List<KhoaHoc> getTopPurchasedCourses(int count) {
        Pageable pageable = PageRequest.of(0, count);
        List<Integer> topIds = khoaHocRepository.findTopPurchasedCourseIds(pageable);

        if (topIds.isEmpty())
            return Collections.emptyList();

        List<KhoaHoc> courses = khoaHocRepository.findByIdInWithDetails(topIds);

        return courses.stream()
                .sorted(Comparator.comparing(course -> topIds.indexOf(course.getKhoahocId())))
                .toList();
    }

    @Override
    public List<KhoaHoc> getEnrolledCourses(String email) {
        return khoaHocRepository.findEnrolledCoursesByEmail(email, TrangThaiGiaoDich.HOAN_THANH);
    }

    @Override
    @Transactional
    public boolean toggleLike(Integer khoahocId, Integer taikhoanId) {
        KhoaHoc khoaHoc = khoaHocRepository.findById(khoahocId).orElse(null);
        TaiKhoan taiKhoan = taiKhoanRepository.findById(taikhoanId).orElse(null);

        if (khoaHoc == null || taiKhoan == null)
            return false;

        Optional<NguoiDungThichKhoaHoc> like = nguoiDungThichKhoaHocRepository
                .findByTaiKhoan_TaikhoanIdAndKhoaHoc_KhoahocId(taikhoanId, khoahocId);

        if (like.isPresent()) {
            nguoiDungThichKhoaHocRepository.delete(like.get());
            khoaHoc.setLuotThich(Math.max((khoaHoc.getLuotThich() != null ? khoaHoc.getLuotThich() : 0) - 1, 0));
            khoaHocRepository.save(khoaHoc);
            return false;
        } else {
            NguoiDungThichKhoaHoc newLike = new NguoiDungThichKhoaHoc(null, taiKhoan, khoaHoc);
            nguoiDungThichKhoaHocRepository.save(newLike);
            khoaHoc.setLuotThich((khoaHoc.getLuotThich() != null ? khoaHoc.getLuotThich() : 0) + 1);
            khoaHocRepository.save(khoaHoc);
            return true;
        }
    }

    @Override
    public List<KhoaHoc> findLikedCoursesByAccountId(Integer currentUserId) {
        if (currentUserId == null)
            return Collections.emptyList();
        return khoaHocRepository.findLikedCoursesByAccountId(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public KhoaHoc getKhoaHocByIdWithDetails(Integer id) {
        return khoaHocRepository.findByIdWithDetails(id).orElse(null);
    }

    @Override
    public Optional<KhoaHoc> findById(Integer id) {
        return khoaHocRepository.findById(id);
    }

    @Override
    public KhoaHoc save(KhoaHoc khoaHoc) {
        return khoaHocRepository.save(khoaHoc);
    }

    @Override
    public List<KhoaHoc> timKiemTheoTen(String tenKhoaHoc) {
        if (tenKhoaHoc == null || tenKhoaHoc.trim().isEmpty()) {
            return khoaHocRepository.findAllActive(TrangThaiKhoaHoc.PUBLISHED); // hoặc DANG_HOAT_DONG
        }
        return khoaHocRepository.findByTenKhoaHocContainingSimple(tenKhoaHoc.trim(), TrangThaiKhoaHoc.PUBLISHED);
    }

    @Override
    public List<KhoaHoc> layKhoaHocDeXuat(int soLuong) {
        List<KhoaHoc> all = khoaHocRepository.findAllActive(TrangThaiKhoaHoc.PUBLISHED);
        return all.stream().limit(soLuong).toList();
    }

    @Override
    public List<DanhMuc> getDanhMucCoKhoaHoc() {
        return danhMucRepository.findAll().stream()
                .filter(dm -> !getKhoaHocTheoDanhMuc(dm.getDanhmucId()).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<DanhMuc> getTop6DanhMucCoKhoaHoc() {
        return danhMucRepository.findTopDanhMucCoNhieuKhoaHoc(PageRequest.of(0, 6));
    }

    //
    @Override
    public List<KhoaHoc> timKiemTheoTenPublished(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return khoaHocRepository.timTheoTenVaTrangThaiPublished(keyword.trim());
        }
        return khoaHocRepository.timTheoTenVaTrangThaiPublished(null);
    }

    @Override
    public List<KhoaHoc> layKhoaHocTheoTrangThai(TrangThaiKhoaHoc trangThai) {
        return khoaHocRepository.findByTrangThai(trangThai);
    }

    @Override
    public KhoaHoc getKhoaHocBySlug(String slug) {
        return khoaHocRepository.findBySlug(slug).orElse(null);
    }

    @Override
    public List<KhoaHoc> findByIds(List<Integer> ids) {
        return khoaHocRepository.findAllByKhoahocIdIn(ids);
    }

    @Override
    public List<KhoaHoc> findAllByIds(List<Integer> ids) {
        return khoaHocRepository.findAllById(ids);
    }

    @Override
    public KhoaHoc layTheoId(Integer id) {
        return khoaHocRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + id));
    }

    @Override
    public List<KhoaHoc> timTheoTenVaGiangVien(Integer giangvienId, String keyword) {
        return khoaHocRepository.timKiemTheoTenVaGiangVien(giangvienId, keyword);
    }

    @Override
    public Page<KhoaHoc> getTatCaKhoaHocPage(Pageable pageable) {
        return khoaHocRepository.findAll(pageable);
    }

    @Override
    public Page<KhoaHoc> getKhoaHocTheoDanhMucPaged(Integer danhMucId, Pageable pageable) {
        return khoaHocRepository.findByDanhMuc_DanhmucId(danhMucId, pageable);
    }

    @Override
    public List<KhoaHoc> getKhoaHocByGiangVienIdAndTrangThai(Integer giangVienId, TrangThaiKhoaHoc trangThai) {
        return khoaHocRepository.findByGiangVien_GiangvienIdAndTrangThai(giangVienId, trangThai);
    }

    @Override
    public List<KhoaHoc> findByGiangVienId(Integer giangVienId) {
        return khoaHocRepository.findByGiangVien_GiangvienId(giangVienId);
    }

    @Override
    public int countHocVien(Integer khoaHocId) {
        return dangHocRepository.countByKhoahoc_KhoahocIdAndTrangthaiTrue(khoaHocId);
    }

    @Override
    public double tinhDoanhThu(Integer khoaHocId) {
        Double sum = dangHocRepository.sumDoanhThuByKhoaHocId(khoaHocId);
        return sum != null ? sum : 0.0;
    }

    @Override
    public List<KhoaHoc> getKhoaHocByGiangVien(int giangVienId) {
        return khoaHocRepository.findByGiangVien_GiangvienId(giangVienId);
    }

    @Override
    public List<KhoaHoc> getTatCaKhoaHocDaXuatBan() {
        return khoaHocRepository.findByTrangThai(TrangThaiKhoaHoc.PUBLISHED);
    }
}
