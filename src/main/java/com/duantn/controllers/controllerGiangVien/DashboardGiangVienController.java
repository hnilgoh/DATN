package com.duantn.controllers.controllerGiangVien;

import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.entities.GiangVien;
import com.duantn.repositories.GiangVienRepository;
import com.duantn.services.AuthService;
import com.duantn.services.GiangVienService;
import com.duantn.services.KhoaHocService;
import com.duantn.services.ViGiangVienService;
import com.duantn.enums.TrangThaiKhoaHoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardGiangVienController {

    @Autowired
    private GiangVienRepository giangVienRepository;

    @Autowired
    private GiangVienService giangVienService;

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private ViGiangVienService viGiangVienService;

    @Autowired
    private AuthService authService;

    @GetMapping("/giangvien/trang-giang-vien")
    public String homegiangvien(Model model,
            @RequestParam(value = "daHuy", required = false) Boolean daHuy) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(auth);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien giangVien = giangVienRepository.findByTaikhoan_TaikhoanId(taiKhoan.getTaikhoanId()).orElse(null);
        if (giangVien == null) {
            return "redirect:/dang-ky-giang-vien";
        }

        Integer giangVienId = giangVien.getGiangvienId();

        List<KhoaHoc> khoaHocList = khoaHocService
                .getKhoaHocByGiangVienIdAndTrangThai(giangVien.getGiangvienId(), TrangThaiKhoaHoc.PUBLISHED);

        List<KhoaHoc> ListDraft = khoaHocService
                .getKhoaHocByGiangVienIdAndTrangThai(giangVien.getGiangvienId(), TrangThaiKhoaHoc.DRAFT);

        List<KhoaHoc> ListPendingApproval = khoaHocService
                .getKhoaHocByGiangVienIdAndTrangThai(giangVien.getGiangvienId(), TrangThaiKhoaHoc.PENDING_APPROVAL);

        // double tongTienNhan =
        // giangVienService.layTongTienNhan(giangVien.getGiangvienId());
        long tongHocVien = giangVienService.demHocVienTheoGiangVien(giangVienId);
        int soKhoaHoc = khoaHocList.size();

        model.addAttribute("tongTienNhan", viGiangVienService.tinhSoDu(taiKhoan));
        model.addAttribute("tongHocVien", tongHocVien);
        model.addAttribute("soKhoaHoc", soKhoaHoc);
        model.addAttribute("daHuy", daHuy != null && daHuy);
        model.addAttribute("ListDraft", ListDraft != null ? ListDraft : new ArrayList<>());
        model.addAttribute("ListPendingApproval",
                ListPendingApproval != null ? ListPendingApproval : new ArrayList<>());
        model.addAttribute("khoaHocList", khoaHocList != null ? khoaHocList : new ArrayList<>());

        return "views/gdienGiangVien/home";
    }

    @PostMapping("/giang-vien/huy-yeu-cau/{id}")
    public String huyYeuCau(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        KhoaHoc khoaHoc = khoaHocService.findById(id).get();
        if (khoaHoc != null) {
            khoaHoc.setTrangThai(TrangThaiKhoaHoc.DRAFT);
            khoaHocService.save(khoaHoc);
        }
        redirectAttributes.addAttribute("daHuy", true);
        return "redirect:/giangvien/trang-giang-vien";
    }
}