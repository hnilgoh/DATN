package com.duantn.repositories;

import com.duantn.entities.ThuNhapNenTang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThuNhapNenTangRepository extends JpaRepository<ThuNhapNenTang, Integer> {
    @Query("SELECT SUM(t.sotiennhan) FROM ThuNhapNenTang t")
    Double tongTienNenTang();

    @Query("SELECT COALESCE(SUM(t.sotiennhan), 0) FROM ThuNhapNenTang t")
    BigDecimal getTongThuNhap();

    @Query("SELECT COALESCE(SUM(t.sotiennhan), 0) FROM ThuNhapNenTang t WHERE t.ngaynhan BETWEEN :start AND :end")
    BigDecimal getTongThuNhapTrongKhoang(LocalDateTime start, LocalDateTime end);

    List<ThuNhapNenTang> findByNgaynhanBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.sotiennhan) FROM ThuNhapNenTang t WHERE YEAR(t.ngaynhan) = :year AND MONTH(t.ngaynhan) = :month")
    BigDecimal tongDoanhThuTheoThang(@Param("year") int year, @Param("month") int month);

}