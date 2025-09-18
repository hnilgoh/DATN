package com.duantn.repositories;

import com.duantn.entities.GiaoDichKhoaHocChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiaoDichKhoaHocChiTietRepository extends JpaRepository<GiaoDichKhoaHocChiTiet, Integer> {

       // Query đơn giản hơn để test
       @Query("SELECT gdct FROM GiaoDichKhoaHocChiTiet gdct " +
                     "JOIN FETCH gdct.giaoDichKhoaHoc gd " +
                     "JOIN FETCH gdct.khoahoc kh " +
                     "ORDER BY gd.ngayGiaoDich DESC")
       List<GiaoDichKhoaHocChiTiet> findAllWithDetails();

       // Query theo accountId (sẽ sử dụng sau khi có authentication)
       @Query("SELECT gdct FROM GiaoDichKhoaHocChiTiet gdct " +
                     "JOIN FETCH gdct.giaoDichKhoaHoc gd " +
                     "JOIN FETCH gdct.khoahoc kh " +
                     "WHERE gd.taikhoan.taikhoanId = :accountId " +
                     "ORDER BY gd.ngayGiaoDich DESC")
       List<GiaoDichKhoaHocChiTiet> findByAccountId(@Param("accountId") Integer accountId);
}