package com.duantn.controllers.controllerNhanVien;

import com.duantn.entities.DangHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.DangHocRepository;
import com.duantn.repositories.TaiKhoanRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/{prefix:(?:admin|nhanvien)}/quanly-hocvien")
@RequiredArgsConstructor
public class QuanLyHocVienController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final DangHocRepository dangHocRepository;

    @GetMapping
    public String danhSachHocVienDaDangKy(@PathVariable String prefix, Model model) {
        List<TaiKhoan> hocVienList = taiKhoanRepository.findTatCaNguoiDungDaDangKyHoc();
        model.addAttribute("hocVienList", hocVienList);
        model.addAttribute("prefix", prefix);
        return "views/gdienQuanLy/danhsachhocvien";
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

        return "redirect:/" + prefix + "/quanly-hocvien";
    }

    @GetMapping("/{id}/khoahoc")
    @ResponseBody
    public List<Map<String, Object>> getKhoaHocDaDangKy(@PathVariable Integer id) {
        List<DangHoc> list = dangHocRepository.findByTaikhoan_TaikhoanId(id);

        List<Map<String, Object>> result = new ArrayList<>();
        for (DangHoc dh : list) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", dh.getKhoahoc().getKhoahocId());
            data.put("tenKhoaHoc", dh.getKhoahoc().getTenKhoaHoc());
            data.put("ngayDangKy", dh.getNgayDangKy().toString());
            data.put("dongia", dh.getDongia());
            data.put("trangThai", dh.isTrangthai() ? "Hoàn thành" : "Đang học");
            data.put("daCapChungChi", dh.isDaCap_ChungChi() ? "Đã cấp" : "Chưa cấp");
            result.add(data);
        }
        return result;
    }

}
