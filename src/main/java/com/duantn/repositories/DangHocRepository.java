package com.duantn.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.dtos.HocVienDto;
import com.duantn.dtos.HocVienTheoKhoaHocDto;
import com.duantn.entities.DangHoc;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;

@Repository
public interface DangHocRepository extends JpaRepository<DangHoc, Integer> {
        @Query("SELECT dh FROM DangHoc dh JOIN FETCH dh.khoahoc WHERE dh.taikhoan.taikhoanId = :id")
        List<DangHoc> findByTaikhoanIdWithKhoaHoc(@Param("id") Integer id);

        long countByKhoahoc_KhoahocId(Integer khoahocId);

        List<DangHoc> findByKhoahoc(KhoaHoc khoaHoc);

        List<DangHoc> findByKhoahocIn(List<KhoaHoc> khoaHocList); // <- Thêm dòng này

        DangHoc findByTaikhoan_TaikhoanIdAndKhoahoc_KhoahocId(Integer taiKhoanId, Integer khoaHocId);

        // Lấy danh sách các khóa học có học viên (theo giảng viên)
        @Query("""
                         SELECT new com.duantn.dtos.HocVienTheoKhoaHocDto(
                             kh.khoahocId,
                             kh.tenKhoaHoc,
                             COUNT(dh)
                         )
                         FROM DangHoc dh
                         JOIN dh.khoahoc kh
                         WHERE kh.giangVien.giangvienId = :giangVienId
                         GROUP BY kh.khoahocId, kh.tenKhoaHoc
                        """)
        List<HocVienTheoKhoaHocDto> demSoHocVienTheoKhoaHoc(@Param("giangVienId") Integer giangVienId);

        // Lấy danh sách học viên theo khóa học
        @Query("""
                         SELECT new com.duantn.dtos.HocVienDto(
                             tk.taikhoanId,
                             tk.name,
                             tk.email,
                             tk.phone
                         )
                         FROM DangHoc dh
                         JOIN dh.taikhoan tk
                         JOIN dh.khoahoc kh
                         WHERE kh.khoahocId = :khoaHocId
                        """)
        List<HocVienDto> findHocVienTheoKhoaHoc(@Param("khoaHocId") Integer khoaHocId);

        @Query("SELECT dh.khoahoc FROM DangHoc dh WHERE dh.taikhoan.taikhoanId = :hocVienId")
        List<KhoaHoc> findKhoaHocByHocVienId(@Param("hocVienId") Integer hocVienId);

        // tổng hoc viên đã đăng ký
        @Query("SELECT COUNT(dh) " +
                        "FROM DangHoc dh " +
                        "JOIN dh.khoahoc kh " +
                        "JOIN kh.giangVien gv " +
                        "WHERE gv.giangvienId = :giangVienId")
        long demSoHocVienTheoGiangVien(@Param("giangVienId") Integer giangVienId);

        boolean existsByKhoahoc_KhoahocId(Integer khoahocId);

        //
        @Query("SELECT d.khoahoc.tenKhoaHoc, COUNT(d) " +
                        "FROM DangHoc d " +
                        "WHERE d.khoahoc.giangVien = :giangVien " +
                        "GROUP BY d.khoahoc.tenKhoaHoc " +
                        "ORDER BY COUNT(d) DESC")
        List<Object[]> findTop5ByGiangVien(@Param("giangVien") GiangVien giangVien, Pageable pageable);

        //
        @Query("SELECT d.khoahoc.tenKhoaHoc, COUNT(d) " +
                        "FROM DangHoc d " +
                        "GROUP BY d.khoahoc.tenKhoaHoc " +
                        "ORDER BY COUNT(d) DESC")
        List<Object[]> findTop5KhoaHoc(Pageable pageable);

        @Query("SELECT COUNT(DISTINCT dh.taikhoan) FROM DangHoc dh")
        int countHocVienDaDangKy();

        @Query("SELECT dh.khoahoc.giangVien.taikhoan.name, COUNT(dh) " +
                        "FROM DangHoc dh " +
                        "GROUP BY dh.khoahoc.giangVien.taikhoan.name " +
                        "ORDER BY COUNT(dh) DESC")
        List<Object[]> findTop5GiangVienHocVien(Pageable pageable);

        int countByKhoahoc_KhoahocIdAndTrangthaiTrue(Integer khoaHocId);

        @Query("SELECT SUM(d.dongia) FROM DangHoc d WHERE d.khoahoc.khoahocId = :khoaHocId AND d.trangthai = true")
        Double sumDoanhThuByKhoaHocId(Integer khoaHocId);

        List<DangHoc> findByTaikhoan_TaikhoanId(Integer taikhoanId);

}
