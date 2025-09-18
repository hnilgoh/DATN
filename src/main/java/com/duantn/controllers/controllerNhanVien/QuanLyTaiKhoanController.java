package com.duantn.controllers.controllerNhanVien;

import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/{prefix:(?:admin|nhanvien)}/quanly-taikhoan")
@RequiredArgsConstructor
public class QuanLyTaiKhoanController {

        private final TaiKhoanRepository taiKhoanRepository;
        private final RoleRepository roleRepository;

        @GetMapping
        public String danhSachHocVien(@PathVariable String prefix, Model model) {
                Role hocVienRole = roleRepository.findByName("ROLE_HOCVIEN")
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy role học viên"));

                List<TaiKhoan> taiKhoanList = taiKhoanRepository.findHocVienChuaDangKy(hocVienRole);
                model.addAttribute("taiKhoanList", taiKhoanList);
                model.addAttribute("prefix", prefix);
                return "views/gdienQuanLy/danhsachtaikhoan";
        }

        @PostMapping("/toggle-status/{id}")
        public String toggleStatus(@PathVariable String prefix,
                        @PathVariable("id") Integer id,
                        RedirectAttributes redirectAttributes) {
                TaiKhoan hocVien = taiKhoanRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên"));

                hocVien.setStatus(!hocVien.isStatus());
                taiKhoanRepository.save(hocVien);

                redirectAttributes.addFlashAttribute("success", hocVien.isStatus()
                                ? "Tài khoản đã được mở khóa!"
                                : "Tài khoản đã bị khóa!");

                return "redirect:/" + prefix + "/quanly-taikhoan";
        }
}
