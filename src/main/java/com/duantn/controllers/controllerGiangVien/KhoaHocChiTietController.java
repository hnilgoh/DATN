package com.duantn.controllers.controllerGiangVien;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.duantn.entities.Chuong;
import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.AuthService;
import com.duantn.services.ChuongService;
import com.duantn.services.DangHocService;
import com.duantn.services.DanhGiaService;
import com.duantn.services.KhoaHocService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class KhoaHocChiTietController {

    @Autowired
    private DangHocService dangHocService;

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private ChuongService chuongService;

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private AuthService authService;

    @GetMapping("/noi-dung-khoa-hoc/{id}")
    public String chiTietKhoaHoc(@PathVariable("id") Integer id, Model model, HttpServletRequest request,
            @RequestParam(required = false) String back) {
        String currentUri = request.getRequestURI();
        model.addAttribute("redirectUrl", currentUri);

        KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(id);
        if (khoaHoc == null) {
            return "redirect:/khoaHoc?error=notfound";
        }

        List<Chuong> chuongs = chuongService.findByKhoaHocId(id);
        long soLuongDangKy = dangHocService.demSoLuongDangKy(id);
        long soLuongDanhGia = danhGiaService.demSoLuongDanhGia(id);
        Double diemTrungBinh = danhGiaService.diemTrungBinh(id);
        System.out.println("Chi tiết khóa học: ID = " + id + ", Back = " + back);

        DanhGia danhGia = new DanhGia(); // mặc định

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Sử dụng AuthService để lấy TaiKhoan, hỗ trợ cả login thường & OAuth2
        TaiKhoan user = authService.getTaiKhoanFromAuth(authentication);

        if (user != null) {
            danhGia = danhGiaService.findByTaikhoanAndKhoahoc(user, khoaHoc).orElse(new DanhGia());
            boolean isEnrolled = dangHocService.isEnrolled(user.getTaikhoanId(), id);
            model.addAttribute("isEnrolled", isEnrolled);
        }

        model.addAttribute("khoaHoc", khoaHoc);
        model.addAttribute("chuongs", chuongs);
        model.addAttribute("soLuongDangKy", soLuongDangKy);
        model.addAttribute("soLuongDanhGia", soLuongDanhGia);
        model.addAttribute("diemTrungBinh", diemTrungBinh);
        model.addAttribute("danhGiaList", danhGiaService.findByKhoaHocId(id));
        model.addAttribute("danhGiaMoi", danhGia);
        model.addAttribute("back", back);

        return "views/KhoaHoc/khoa-hoc-giang-vien";
    }
}