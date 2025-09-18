package com.duantn.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.DoanhThuGiangVien;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiDoanhThu;

@Repository
public interface DoanhThuGiangVienRepository extends JpaRepository<DoanhThuGiangVien, Integer> {

        @Query("SELECT COALESCE(SUM(d.sotiennhan), 0) FROM DoanhThuGiangVien d " +
                        "WHERE d.taikhoanGV.taikhoanId = :taiKhoanId AND d.trangthai = 'DA_NHAN'")
        BigDecimal tongTienNhanTheoGiangVien(@Param("taiKhoanId") Integer taiKhoanId);

        @Query("SELECT COALESCE(SUM(d.sotiennhan), 0) " +
                        "FROM DoanhThuGiangVien d " +
                        "WHERE d.taikhoanGV.taikhoanId = :taiKhoanId " +
                        "AND d.ngaynhan BETWEEN :startDate AND :endDate " +
                        "AND d.trangthai = :trangThai")
        BigDecimal tinhDoanhThuTrongKhoangNgay(@Param("taiKhoanId") Integer taiKhoanId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("trangThai") TrangThaiDoanhThu trangThai);

        List<DoanhThuGiangVien> findByTaikhoanGV(TaiKhoan taiKhoan);

        //
        @Query("SELECT COALESCE(SUM(d.sotiennhan), 0) " +
                        "FROM DoanhThuGiangVien d " +
                        "WHERE MONTH(d.ngaynhan) = MONTH(CURRENT_DATE) " +
                        "AND YEAR(d.ngaynhan) = YEAR(CURRENT_DATE) " +
                        "AND d.taikhoanGV = :giangVien " +
                        "AND d.trangthai = 'DA_NHAN'")
        BigDecimal getTongThuTrongThang(@Param("giangVien") TaiKhoan giangVien);

        @Query("SELECT COUNT(d) " +
                        "FROM DoanhThuGiangVien d " +
                        "WHERE MONTH(d.ngaynhan) = MONTH(CURRENT_DATE) " +
                        "AND YEAR(d.ngaynhan) = YEAR(CURRENT_DATE) " +
                        "AND d.taikhoanGV = :giangVien " +
                        "AND d.trangthai = 'DA_NHAN'")
        Long getSoLanNhanTrongThang(@Param("giangVien") TaiKhoan giangVien);

        @Query("SELECT d.tenGiangVien, SUM(d.sotiennhan) " +
                        "FROM DoanhThuGiangVien d " +
                        "GROUP BY d.tenGiangVien " +
                        "ORDER BY SUM(d.sotiennhan) DESC")
        List<Object[]> findTop3GiangVienDoanhThu(Pageable pageable);

        List<DoanhThuGiangVien> findByTaikhoanGV_TaikhoanId(Integer taikhoanId);

        @Query("SELECT COALESCE(SUM(d.sotiennhan), 0) FROM DoanhThuGiangVien d WHERE d.taikhoanGV.id = :taikhoanId")
        BigDecimal tinhTongDoanhThuTheoGiangVien(@Param("taikhoanId") Integer taikhoanId);

        @Query("SELECT MONTH(d.ngaynhan), SUM(d.sotiennhan) " +
                        "FROM DoanhThuGiangVien d " +
                        "WHERE d.taikhoanGV = :giangVien " +
                        "AND d.trangthai = 'DA_NHAN' " +
                        "GROUP BY MONTH(d.ngaynhan) " +
                        "ORDER BY MONTH(d.ngaynhan)")
        List<Object[]> thongKeDoanhThuTheoThang(@Param("giangVien") TaiKhoan giangVien);

}
