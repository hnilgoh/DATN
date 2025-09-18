package com.duantn.controllers.controllerGiangVien;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.duantn.entities.DangHoc;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.DangHocRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.TienDoHocService;
import org.springframework.data.domain.PageRequest;

@Controller
public class XemHocVienController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepository;

    @Autowired
    private DangHocRepository dangHocRepository;

    @Autowired
    private TienDoHocService tienDoHocService; // ✅ THÊM SERVICE

    @GetMapping("/giangvien/quan-ly-hoc-vien")
    public String xemDanhSachHocVien(
            @RequestParam(name = "khoahocId", required = false) Integer khoahocId, Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/auth/dangnhap";
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(principal.getName())
                .orElse(null);

        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien giangVien = taiKhoan.getGiangVien();
        if (giangVien == null) {
            return "redirect:/auth/dangnhap";
        }

        List<KhoaHoc> khoaHocList = khoaHocRepository.findByGiangVien(giangVien);
        model.addAttribute("khoaHocList", khoaHocList);

        List<DangHoc> dangHocList;

        if (khoahocId != null) {
            Optional<KhoaHoc> khoaHocOpt = khoaHocRepository.findById(khoahocId);
            if (khoaHocOpt.isPresent() && khoaHocOpt.get().getGiangVien().getGiangvienId()
                    .equals(giangVien.getGiangvienId())) {
                dangHocList = dangHocRepository.findByKhoahoc(khoaHocOpt.get());
                model.addAttribute("selectedKhoaHocId", khoahocId);
            } else {
                dangHocList = List.of();
            }
        } else {
            dangHocList = dangHocRepository.findByKhoahocIn(khoaHocList);
        }

        Map<Integer, Integer> tienDoPhanTramMap = new HashMap<>();
        for (DangHoc dangHoc : dangHocList) {
            int khoaHocId = dangHoc.getKhoahoc().getKhoahocId();
            int taiKhoanId = dangHoc.getTaikhoan().getTaikhoanId();
            int phanTram = tienDoHocService.tinhTienDoPhanTram(taiKhoanId, khoaHocId);
            tienDoPhanTramMap.put(dangHoc.getDanghocId(), phanTram);
        }

        model.addAttribute("dangHocList", dangHocList);
        model.addAttribute("tienDoPhanTramMap", tienDoPhanTramMap);
        model.addAttribute("taiKhoan", taiKhoan);

        // 📊 DỮ LIỆU BIỂU ĐỒ TOP 5 KHÓA HỌC
        List<Object[]> topKhoaHocList = dangHocRepository.findTop5ByGiangVien(giangVien, PageRequest.of(0, 5));

        List<String> topKhoaHocLabels = new ArrayList<>();
        List<Integer> topKhoaHocSoLuong = new ArrayList<>();

        for (Object[] obj : topKhoaHocList) {
            topKhoaHocLabels.add((String) obj[0]);
            topKhoaHocSoLuong.add(((Number) obj[1]).intValue());
        }

        model.addAttribute("topKhoaHocLabels", topKhoaHocLabels);
        model.addAttribute("topKhoaHocSoLuong", topKhoaHocSoLuong);

        return "views/gdienGiangVien/quan-ly-hoc-vien";
    }
}