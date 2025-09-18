package com.duantn.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duantn.entities.GioHang;
import com.duantn.entities.GioHangChiTiet;
import com.duantn.entities.KhoaHoc;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    Optional<GioHangChiTiet> findByGiohangAndKhoahoc(GioHang gioHang, KhoaHoc khoaHoc);

    List<GioHangChiTiet> findByGiohang(GioHang giohang);

}
