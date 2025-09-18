package com.duantn.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    List<DanhGia> findByKhoahoc_KhoahocIdOrderByNgayDanhGiaDesc(Integer khoaHocId);

    boolean existsByKhoahoc_KhoahocIdAndTaikhoan_TaikhoanId(Integer khoaHocId, Integer taiKhoanId);

    @Query("SELECT AVG(d.diemDanhGia) FROM DanhGia d WHERE d.khoahoc.khoahocId = :khoaHocId")
    Double trungBinhDanhGia(@Param("khoaHocId") Integer khoaHocId);

    Optional<DanhGia> findByTaikhoanAndKhoahoc(TaiKhoan taikhoan, KhoaHoc khoahoc);

    long countByKhoahoc_KhoahocId(Integer khoahocId);

    @Query("SELECT AVG(d.diemDanhGia) FROM DanhGia d WHERE d.khoahoc.khoahocId = :khoaHocId")
    Double tinhDiemTrungBinhTheoKhoaHoc(@Param("khoaHocId") Integer khoaHocId);

    @Query("SELECT d FROM DanhGia d WHERE d.khoahoc.khoahocId = :khoaHocId ORDER BY d.ngayDanhGia DESC")
    List<DanhGia> findByKhoaHocId(@Param("khoaHocId") Integer khoaHocId);

    List<DanhGia> findByKhoahoc_GiangVien_GiangvienId(Integer giangvienId);
}
