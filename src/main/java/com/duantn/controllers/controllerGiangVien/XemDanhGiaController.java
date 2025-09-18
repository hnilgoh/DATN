
package com.duantn.controllers.controllerGiangVien;

import com.duantn.entities.DanhGia;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.services.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("giangvien/danh-gia-tu-khoa-hoc")
public class XemDanhGiaController {

    private final KhoaHocRepository khoaHocRepository;
    private final AuthService authService;

    @GetMapping
    public String xemDanhGiaTongQuan(
            @RequestParam(required = false) Integer khoahocId,
            @RequestParam(required = false) Integer sao,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien gv = taiKhoan.getGiangVien();
        if (gv == null) {
            return "redirect:/auth/dangnhap";
        }

        List<KhoaHoc> khoaHocs = khoaHocRepository.findByGiangVien(gv);
        model.addAttribute("dsKhoaHoc", khoaHocs);
        model.addAttribute("saoDaChon", sao);
        model.addAttribute("khoaHocDaChon", khoahocId);

        if (khoahocId != null) {
            Optional<KhoaHoc> khOpt = khoaHocRepository.findById(khoahocId);
            if (khOpt.isPresent() && khOpt.get().getGiangVien().equals(gv)) {
                KhoaHoc kh = khOpt.get();
                List<DanhGia> danhGias = kh.getDanhGiaList();

                if (sao != null) {
                    danhGias = danhGias.stream()
                            .filter(dg -> dg.getDiemDanhGia() == sao)
                            .toList();
                }

                model.addAttribute("khoaHocChiTiet", kh);
                model.addAttribute("dsDanhGia", danhGias);

                double diemTB = danhGias.isEmpty() ? 0.0
                        : danhGias.stream().mapToInt(DanhGia::getDiemDanhGia).average().orElse(0.0);

                model.addAttribute("diemTrungBinh", diemTB);
                model.addAttribute("tongLuot", danhGias.size());
            }
        } else {
            List<Map<String, Object>> tongQuan = new ArrayList<>();
            for (KhoaHoc kh : khoaHocs) {
                List<DanhGia> danhGias = kh.getDanhGiaList();
                if (danhGias.isEmpty())
                    continue;

                double avg = danhGias.stream().mapToInt(DanhGia::getDiemDanhGia).average().orElse(0.0);

                Map<String, Object> item = new HashMap<>();
                item.put("khoahoc", kh);
                item.put("tong", danhGias.size());
                item.put("diemTB", avg);
                tongQuan.add(item);
            }
            model.addAttribute("tongQuanDanhGia", tongQuan);
        }

        return "views/gdienGiangVien/quan-ly-danh-gia";
    }
}
