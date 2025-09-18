package com.duantn.controllers.controllerGiangVien;

import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.repositories.GiangVienRepository;
import com.duantn.services.KhoaHocService;
import com.duantn.services.AuthService;
import com.duantn.services.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/giangvien")
public class KhoaHocDaTaoController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/khoa-hoc-da-tao")
    public String khoaHocDaTao(Model model, @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "daHuy", required = false) Boolean daHuy) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);

        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        if (!taiKhoan.isStatus()) {
            model.addAttribute("error", "Tài khoản của bạn đã gặp lỗi.");
            return "redirect:/auth/dangnhap";
        }

        GiangVien giangVien = giangVienRepository.findByTaikhoan_TaikhoanId(taiKhoan.getTaikhoanId()).orElse(null);
        if (giangVien == null) {
            model.addAttribute("error", "Không tìm thấy thông tin giảng viên.");
            return "redirect:/auth/dangnhap";
        }

        int giangVienId = giangVien.getGiangvienId();

        model.addAttribute("listPublished",
                khoaHocService.getKhoaHocByGiangVienIdAndTrangThai(giangVienId, TrangThaiKhoaHoc.PUBLISHED));
        model.addAttribute("listDraft",
                khoaHocService.getKhoaHocByGiangVienIdAndTrangThai(giangVienId, TrangThaiKhoaHoc.DRAFT));
        model.addAttribute("listPendingApproval",
                khoaHocService.getKhoaHocByGiangVienIdAndTrangThai(giangVienId, TrangThaiKhoaHoc.PENDING_APPROVAL));
        model.addAttribute("listUnpublished",
                khoaHocService.getKhoaHocByGiangVienIdAndTrangThai(giangVienId, TrangThaiKhoaHoc.UNPUBLISHED));

        model.addAttribute("daHuy", daHuy != null && daHuy);

        return "views/gdienGiangVien/khoa-hoc-da-tao";
    }

    @PostMapping("/huy-yeu-cau-khoa-hoc/{id}")
    public String huyYeuCautab2(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        KhoaHoc khoaHoc = khoaHocService.findById(id).get();
        if (khoaHoc != null) {
            khoaHoc.setTrangThai(TrangThaiKhoaHoc.DRAFT);
            khoaHocService.save(khoaHoc);
        }
        redirectAttributes.addAttribute("daHuy", true);
        return "redirect:/giangvien/khoa-hoc-da-tao";
    }
}
