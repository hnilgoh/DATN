package com.duantn.controllers.controllerAdmin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duantn.entities.TaiKhoan;
import com.duantn.entities.ThongBao;
import com.duantn.enums.LoaiThongBao;
import com.duantn.repositories.ThongBaoRepository;
import com.duantn.services.AuthService;

@Controller
@RequestMapping("/quan-ly/thong-bao-he-thong")
public class ThongBaoQuanLyController {

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private AuthService authService;

    @GetMapping
    public String hienThiDanhSachThongBao(Model model, Authentication authentication) {

        TaiKhoan currentTaiKhoan = authService.getTaiKhoanFromAuth(authentication);

        if (currentTaiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        String role = currentTaiKhoan.getRole().getName();

        if (!role.equalsIgnoreCase("ROLE_ADMIN") && !role.equalsIgnoreCase("ROLE_NHANVIEN")) {
            return "redirect:/access-denied";
        }

        // Thông báo cá nhân gửi riêng
        List<ThongBao> danhSach = thongBaoRepository
                .findAllByNguoiNhanId(currentTaiKhoan.getTaikhoanId());

        // Thêm thông báo chung của hệ thống
        List<ThongBao> heThongThongBaos = thongBaoRepository
                .findAllByLoaiThongBao(LoaiThongBao.HE_THONG);

        // Gộp hai danh sách
        danhSach.addAll(heThongThongBaos);

        model.addAttribute("danhSachThongBao", danhSach);

        return "views/gdienQuanLy/quanly-thong-bao-he-thong";
    }
}
