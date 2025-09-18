package com.duantn.services;

import java.util.List;
import java.util.Optional;

import com.duantn.entities.TaiKhoan;

public interface TaiKhoanService {
    TaiKhoan login(String email, String password);

    TaiKhoan register(TaiKhoan account);

    List<TaiKhoan> layTatCaNhanVien();

    TaiKhoan themNhanVien(TaiKhoan taiKhoan);

    TaiKhoan capNhatNhanVien(Integer id, TaiKhoan taiKhoan);

    void xoaNhanVien(Integer id);

    TaiKhoan layTheoId(Integer id);

    TaiKhoan getCurrentUser();

    TaiKhoan findByEmail(String email);

    Optional<TaiKhoan> findById(Integer id);

    TaiKhoan findByUsername(String username);

    List<TaiKhoan> layTatCaGiangVien();
}
