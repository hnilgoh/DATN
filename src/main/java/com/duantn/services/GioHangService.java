package com.duantn.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duantn.entities.GioHang;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.GioHangRepository;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepo;

    public GioHang getOrCreateGioHang(TaiKhoan taiKhoan) {
        return gioHangRepo.findByTaikhoan(taiKhoan)
                .orElseGet(() -> {
                    GioHang gioHang = GioHang.builder()
                            .taikhoan(taiKhoan)
                            .ngayTao(LocalDateTime.now())
                            .build();
                    return gioHangRepo.save(gioHang);
                });
    }
}
