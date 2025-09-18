package com.duantn.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.duantn.entities.DanhMuc;
import com.duantn.entities.KhoaHoc;
import com.duantn.enums.TrangThaiKhoaHoc;

public interface KhoaHocService {

    List<KhoaHoc> timTheoTenVaGiangVien(Integer giangvienId, String keyword);

    List<KhoaHoc> getTatCaKhoaHoc();

    KhoaHoc getKhoaHocById(Integer id);

    List<KhoaHoc> layTatCaKhoaHocCanDuyet();

    List<KhoaHoc> getNewestCourses(int count);

    List<KhoaHoc> getTopPurchasedCourses(int count);

    List<KhoaHoc> getEnrolledCourses(String email);

    boolean toggleLike(Integer khoahocId, Integer taikhoanId);

    List<KhoaHoc> findLikedCoursesByAccountId(Integer currentUserId);

    KhoaHoc getKhoaHocByIdWithDetails(Integer id);

    Optional<KhoaHoc> findById(Integer id);

    //

    KhoaHoc save(KhoaHoc khoaHoc);

    List<KhoaHoc> timKiemTheoTen(String tenKhoaHoc);

    List<KhoaHoc> layKhoaHocDeXuat(int soLuong);

    List<KhoaHoc> getKhoaHocTheoDanhMuc(Integer danhMucId);

    List<DanhMuc> getDanhMucCoKhoaHoc();

    //
    List<KhoaHoc> timKiemTheoTenPublished(String keyword);

    List<KhoaHoc> layKhoaHocTheoTrangThai(TrangThaiKhoaHoc trangThai);

    KhoaHoc getKhoaHocBySlug(String slug);

    List<KhoaHoc> findByIds(List<Integer> ids);

    List<KhoaHoc> findAllByIds(List<Integer> ids);

    KhoaHoc layTheoId(Integer id);

    Page<KhoaHoc> getTatCaKhoaHocPage(Pageable pageable);

    // Phân trang theo danh mục
    Page<KhoaHoc> getKhoaHocTheoDanhMucPaged(Integer danhMucId, Pageable pageable);

    List<KhoaHoc> getKhoaHocByGiangVienIdAndTrangThai(Integer giangVienId, TrangThaiKhoaHoc trangThai);

    List<KhoaHoc> findByGiangVienId(Integer giangVienId);

    int countHocVien(Integer khoaHocId);

    double tinhDoanhThu(Integer khoaHocId);

    List<KhoaHoc> getKhoaHocByGiangVien(int giangVienId);

    List<DanhMuc> getTop6DanhMucCoKhoaHoc();

    List<KhoaHoc> getTatCaKhoaHocDaXuatBan();
}
