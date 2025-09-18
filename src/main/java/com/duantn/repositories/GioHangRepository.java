package com.duantn.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duantn.entities.GioHang;
import com.duantn.entities.TaiKhoan;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    Optional<GioHang> findByTaikhoan(TaiKhoan taiKhoan);
}