package com.duantn.repositories;

import com.duantn.dtos.KhoaHocDiemDto;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.enums.TrangThaiGiaoDich;
import com.duantn.enums.TrangThaiKhoaHoc;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhoaHocRepository extends JpaRepository<KhoaHoc, Integer> {

        // Dành cho quản trị duyệt khóa học
        List<KhoaHoc> findAllByTrangThai(TrangThaiKhoaHoc trangThai);

        // Dành cho trang chủ - lấy mới nhất
        @EntityGraph(attributePaths = { "giangVien", "giangVien.taikhoan" })
        List<KhoaHoc> findByOrderByNgayTaoDesc(Pageable pageable);

        // Lấy top khóa học được mua nhiều nhất
        @Query("""
                        SELECT gdct.khoahoc.khoahocId
                        FROM GiaoDichKhoaHocChiTiet gdct
                        GROUP BY gdct.khoahoc.khoahocId
                        ORDER BY COUNT(gdct.id) DESC
                        """)
        List<Integer> findTopPurchasedCourseIds(Pageable pageable);

        // Truy vấn danh sách khóa học theo ID (kèm giảng viên, tài khoản)
        @Query("SELECT kh FROM KhoaHoc kh WHERE kh.khoahocId IN :ids")
        @EntityGraph(attributePaths = { "giangVien", "giangVien.taikhoan" })
        List<KhoaHoc> findByIdInWithDetails(@Param("ids") List<Integer> ids);

        // Lấy chi tiết một khóa học (có giảng viên, tài khoản, danh mục)
        @Query("SELECT kh FROM KhoaHoc kh WHERE kh.khoahocId = :id")
        @EntityGraph(attributePaths = { "giangVien.taikhoan", "danhMuc" })
        Optional<KhoaHoc> findByIdWithDetails(@Param("id") Integer id);

        // Lấy danh sách khóa học đã thích
        @Query("SELECT n.khoaHoc FROM NguoiDungThichKhoaHoc n WHERE n.taiKhoan.taikhoanId = :taikhoanId")
        @EntityGraph(attributePaths = { "giangVien", "giangVien.taikhoan" })
        List<KhoaHoc> findLikedCoursesByAccountId(@Param("taikhoanId") Integer taikhoanId);

        // Lấy danh sách khóa học đã đăng ký thành công
        @Query("""
                        SELECT DISTINCT gdct.khoahoc FROM GiaoDichKhoaHocChiTiet gdct
                        JOIN FETCH gdct.khoahoc.giangVien gv
                        JOIN FETCH gv.taikhoan
                        WHERE gdct.giaoDichKhoaHoc.taikhoan.email = :email
                        AND gdct.giaoDichKhoaHoc.trangthai = :status
                        """)
        List<KhoaHoc> findEnrolledCoursesByEmail(@Param("email") String email,
                        @Param("status") TrangThaiGiaoDich status);

        // Lấy khóa học kèm chương và bài giảng (tuỳ chọn nếu cần)
        @Query("SELECT k FROM KhoaHoc k LEFT JOIN FETCH k.chuongs c LEFT JOIN FETCH c.baiGiangs WHERE k.khoahocId = :id")
        Optional<KhoaHoc> findByIdWithChaptersAndLectures(@Param("id") Integer id);

        @Query("SELECT k FROM KhoaHoc k WHERE k.tenKhoaHoc LIKE %:tuKhoa% AND k.trangThai = :trangThai")
        List<KhoaHoc> findByTenKhoaHocContainingSimple(@Param("tuKhoa") String tuKhoa,
                        @Param("trangThai") TrangThaiKhoaHoc trangThai);

        @Query("SELECT k FROM KhoaHoc k WHERE k.trangThai = :trangThai ORDER BY k.ngayTao DESC")
        List<KhoaHoc> findAllActive(@Param("trangThai") TrangThaiKhoaHoc trangThai);

        List<KhoaHoc> findByDanhMuc_DanhmucIdAndTrangThai(Integer danhMucId, TrangThaiKhoaHoc trangThai);

        //
        @Query("""
                        SELECT k FROM KhoaHoc k
                        JOIN FETCH k.giangVien gv
                        WHERE k.trangThai = 'PUBLISHED'
                        AND (:keyword IS NULL OR LOWER(k.tenKhoaHoc) LIKE LOWER(CONCAT('%', :keyword, '%')))
                        """)
        List<KhoaHoc> timTheoTenVaTrangThaiPublished(@Param("keyword") String keyword);

        List<KhoaHoc> findByTrangThai(TrangThaiKhoaHoc trangThai);

        Optional<KhoaHoc> findBySlug(String slug);

        List<KhoaHoc> findByGiangVien(GiangVien giangVien);

        @Query("SELECT SUM(d.dongia) FROM DangHoc d " +
                        "WHERE d.khoahoc.giangVien = :giangVien " +
                        "AND d.trangthai = true")
        BigDecimal tinhTongDoanhThu(GiangVien giangVien);

        List<KhoaHoc> findAllByKhoahocIdIn(List<Integer> ids);

        //
        @Query("SELECT COUNT(kh) FROM KhoaHoc kh WHERE kh.trangThai = :trangThai")
        int countKhoaHocByTrangThai(@Param("trangThai") TrangThaiKhoaHoc trangThai);

        @Query("SELECT kh.danhMuc.tenDanhMuc, COUNT(kh) FROM KhoaHoc kh GROUP BY kh.danhMuc.tenDanhMuc")
        List<Object[]> tiLeDanhMuc();

        @Query("SELECT kh.tenKhoaHoc, COUNT(dh), SUM(gd.tongtien), COUNT(ndth), gv.taikhoan.name FROM KhoaHoc kh LEFT JOIN DangHoc dh ON kh = dh.khoahoc LEFT JOIN GiaoDichKhoaHoc gd ON gd.taikhoan = dh.taikhoan LEFT JOIN NguoiDungThichKhoaHoc ndth ON ndth.khoaHoc = kh LEFT JOIN GiangVien gv ON kh.giangVien = gv GROUP BY kh.tenKhoaHoc, gv.taikhoan.name")
        List<Object[]> chiTietKhoaHoc();

        List<KhoaHoc> findByGiangVien_GiangvienIdAndTrangThai(Integer giangVienId, TrangThaiKhoaHoc trangThai);

        @Query("SELECT new com.duantn.dtos.KhoaHocDiemDto(k.tenKhoaHoc, AVG(d.diemDanhGia)) " +
                        "FROM KhoaHoc k JOIN k.danhGiaList d " +
                        "WHERE k.giangVien.giangvienId = :gvId AND k.trangThai = 'PUBLISHED' " +
                        "GROUP BY k.tenKhoaHoc")
        List<KhoaHocDiemDto> findDiemTrungBinhTheoKhoaHocXuatBan(@Param("gvId") Integer giangVienId);

        @Query("""
                        SELECT k FROM KhoaHoc k
                        JOIN FETCH k.giangVien gv
                        WHERE gv.giangvienId = :giangvienId
                        AND (:keyword IS NULL OR LOWER(k.tenKhoaHoc) LIKE LOWER(CONCAT('%', :keyword, '%')))
                        """)
        List<KhoaHoc> timKiemTheoTenVaGiangVien(@Param("giangvienId") Integer giangVienId,
                        @Param("keyword") String keyword);

        List<KhoaHoc> findByGiangVien_GiangvienId(Integer giangvienId);

        List<KhoaHoc> findByDanhMuc_danhmucId(Integer danhMucId);

        Page<KhoaHoc> findByTrangThai(TrangThaiKhoaHoc trangThai, Pageable pageable);

        Page<KhoaHoc> findByDanhMuc_DanhmucId(Integer danhMucId, Pageable pageable);

        @Query("SELECT kh.danhMuc.tenDanhMuc, COUNT(kh) " +
                        "FROM KhoaHoc kh " +
                        "WHERE kh.trangThai = com.duantn.enums.TrangThaiKhoaHoc.PUBLISHED AND kh.danhMuc IS NOT NULL " +
                        "GROUP BY kh.danhMuc.tenDanhMuc " +
                        "ORDER BY COUNT(kh) DESC")
        List<Object[]> findTopDanhMucBySoLuongKhoaHoc(Pageable pageable);
}