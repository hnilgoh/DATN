package com.duantn.controllers.controllerHocVien;

import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.AuthService;
import com.duantn.services.DanhGiaService;
import com.duantn.services.KhoaHocService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
@RequestMapping("hocvien/danh-gia")
public class DanhGiaController {

    private final KhoaHocService khoaHocService;
    private final DanhGiaService danhGiaService;
    private final AuthService authService;

    @PostMapping("/{khoahocId}")
    public String xuLyGuiDanhGia(@PathVariable("khoahocId") Integer khoahocId,
            @ModelAttribute("danhGiaMoi") DanhGia danhGia) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan nguoiDung = authService.getTaiKhoanFromAuth(auth);

        if (nguoiDung == null) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        KhoaHoc khoaHoc = khoaHocService.findById(khoahocId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        danhGia.setKhoahoc(khoaHoc);
        danhGiaService.taoHoacCapNhatDanhGia(khoaHoc.getKhoahocId(), nguoiDung, danhGia);

        return "redirect:/khoaHoc/" + khoahocId + "?danhgia=ok";
    }

    @PostMapping("/xoa/{danhGiaId}")
    public String xoaDanhGia(@PathVariable("danhGiaId") Integer danhGiaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan nguoiDung = authService.getTaiKhoanFromAuth(auth);

        if (nguoiDung == null) {
            throw new RuntimeException("Bạn cần đăng nhập để xóa đánh giá.");
        }

        Integer khoahocId = danhGiaService.xoaDanhGiaTheoId(danhGiaId, nguoiDung);

        return "redirect:/khoaHoc/" + khoahocId + "?danhgia=deleted";
    }
}