package com.duantn.controllers.controllerChung;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.duantn.entities.DanhMuc;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.AuthService;
import com.duantn.services.DanhMucService;
import com.duantn.services.DangHocService;
import com.duantn.services.KhoaHocService;

@Controller
public class DanhMucKhoaHocController {

    @Autowired
    private DanhMucService danhMucService;

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private DangHocService dangHocService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{slug}")
    public String khoaHocTheoDanhMuc(@PathVariable String slug, Model model, Authentication authentication) {

        DanhMuc danhMuc = danhMucService.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        List<KhoaHoc> ds = khoaHocService.getKhoaHocTheoDanhMuc(danhMuc.getDanhmucId());

        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan != null) {

            boolean isGiangVien = taiKhoan.getGiangVien() != null;

            Set<Integer> khoaHocDaHocIds = ds.stream()
                    .filter(kh -> dangHocService.isEnrolled(taiKhoan.getTaikhoanId(), kh.getKhoahocId()))
                    .map(KhoaHoc::getKhoahocId)
                    .collect(Collectors.toSet());

            Set<Integer> khoaHocTuTaoIds = isGiangVien
                    ? khoaHocService.getKhoaHocByGiangVien(taiKhoan.getGiangVien().getGiangvienId())
                            .stream().map(KhoaHoc::getKhoahocId).collect(Collectors.toSet())
                    : Set.of();

            ds = ds.stream()
                    .filter(kh -> !khoaHocDaHocIds.contains(kh.getKhoahocId()))
                    .filter(kh -> !khoaHocTuTaoIds.contains(kh.getKhoahocId()))
                    .collect(Collectors.toList());

            model.addAttribute("enrolledCourseIds", khoaHocDaHocIds);
        }

        model.addAttribute("danhMuc", danhMuc);
        model.addAttribute("khoaHocList", ds);

        return "views/gdienChung/danhmuckhoahoc";
    }
}
