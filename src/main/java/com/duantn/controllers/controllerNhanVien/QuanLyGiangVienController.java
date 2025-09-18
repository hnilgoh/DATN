package com.duantn.controllers.controllerNhanVien;

import com.duantn.entities.DoanhThuGiangVien;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.DoanhThuGiangVienService;
import com.duantn.services.GiangVienService;
import com.duantn.services.KhoaHocService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/{prefix:(?:admin|nhanvien)}/quanly-giangvien")
@RequiredArgsConstructor
public class QuanLyGiangVienController {

        private final TaiKhoanRepository taiKhoanRepository;
        private final RoleRepository roleRepository;
        private final GiangVienService giangVienService;
        private final KhoaHocService khoaHocService;
        private final DoanhThuGiangVienService doanhThuGiangVienService;

        @GetMapping
        public String danhSach(@PathVariable String prefix, Model model) {
                Role giangVienRole = roleRepository.findByName("ROLE_GIANGVIEN")
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền giảng viên"));

                List<TaiKhoan> giangVienList = taiKhoanRepository.findByRole(giangVienRole);
                model.addAttribute("giangVienList", giangVienList);
                model.addAttribute("prefix", prefix);
                return "views/gdienQuanLy/danhsachgiangvien";
        }

        @PostMapping("/toggle-status/{id}")
        public String toggleStatus(@PathVariable String prefix,
                        @PathVariable("id") Integer id,
                        RedirectAttributes ra) {

                TaiKhoan giangVien = taiKhoanRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên"));

                giangVien.setStatus(!giangVien.isStatus());
                taiKhoanRepository.save(giangVien);

                ra.addFlashAttribute("success", giangVien.isStatus()
                                ? "Tài khoản đã được mở khóa!"
                                : "Tài khoản đã bị khóa!");

                return "redirect:/" + prefix + "/quanly-giangvien";
        }

        @GetMapping("/giangvien/{id}/chitiet")
        public String chiTietGiangVien(@PathVariable String prefix,
                        @PathVariable("id") Integer id,
                        Model model) {
                GiangVien gv = giangVienService.findById(id);

                // Lấy danh sách doanh thu của giảng viên từ entity DoanhThuGiangVien
                List<DoanhThuGiangVien> doanhThuList = doanhThuGiangVienService
                                .findByTaiKhoanGV_Id(gv.getTaikhoan().getTaikhoanId());

                // Tính tổng doanh thu
                BigDecimal tongDoanhThu = doanhThuList.stream()
                                .map(DoanhThuGiangVien::getSotiennhan)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Lấy danh sách khóa học đã xuất bản của giảng viên
                List<KhoaHoc> khoaHocList = khoaHocService.getKhoaHocByGiangVienIdAndTrangThai(gv.getGiangvienId(),
                                TrangThaiKhoaHoc.PUBLISHED);

                model.addAttribute("giangVien", gv);
                model.addAttribute("doanhThuList", doanhThuList);
                model.addAttribute("tongDoanhThu", tongDoanhThu);
                model.addAttribute("khoaHocList", khoaHocList);
                model.addAttribute("prefix", prefix);

                return "views/gdienQuanLy/danhsachgiangvienchitiet";
        }

}