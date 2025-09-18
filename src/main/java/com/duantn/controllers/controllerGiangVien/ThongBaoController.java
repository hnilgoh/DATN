package com.duantn.controllers.controllerGiangVien;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duantn.entities.TaiKhoan;
import com.duantn.entities.ThongBao;
import com.duantn.repositories.ThongBaoRepository;
import com.duantn.services.AuthService;

@Controller
@RequestMapping("/giang-vien/thong-bao")
public class ThongBaoController {

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

        if (!"ROLE_GIANGVIEN".equalsIgnoreCase(currentTaiKhoan.getRole().getName())) {
            return "redirect:/access-denied";
        }

        List<ThongBao> danhSach = thongBaoRepository
                .findAllByNguoiNhanTaikhoanIdOrderByNgayguiDesc(currentTaiKhoan.getTaikhoanId());

        model.addAttribute("danhSachThongBao", danhSach);
        return "views/gdienGiangVien/thong-bao";
    }
}
