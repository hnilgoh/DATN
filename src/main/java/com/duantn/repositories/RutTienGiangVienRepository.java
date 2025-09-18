package com.duantn.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duantn.entities.RutTienGiangVien;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiRutTien;

@Repository
public interface RutTienGiangVienRepository extends JpaRepository<RutTienGiangVien, Integer> {
    List<RutTienGiangVien> findByTaikhoanGVAndTrangthai(TaiKhoan taiKhoan, TrangThaiRutTien trangthai);

    List<RutTienGiangVien> findByTaikhoanGVAndTrangthaiIn(TaiKhoan giangVien, List<TrangThaiRutTien> trangThai);

    Optional<RutTienGiangVien> findTopByTaikhoanGVOrderByNgayrutDesc(TaiKhoan giangVien);

    List<RutTienGiangVien> findByTrangthai(TrangThaiRutTien trangthai);

}
