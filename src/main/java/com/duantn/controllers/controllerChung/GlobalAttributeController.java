package com.duantn.controllers.controllerChung;

import com.duantn.entities.DanhMuc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.NguoiDungThichKhoaHocRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.AuthService;
import com.duantn.services.KhoaHocService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalAttributeController {

    @Autowired
    KhoaHocService khoaHocService;

    @Autowired
    TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    NguoiDungThichKhoaHocRepository nguoiDungThichKhoaHocRepository;

    @ModelAttribute("danhMucAllList")
    public List<DanhMuc> getDanhMucList() {
        return khoaHocService.getDanhMucCoKhoaHoc();
    }

    @ModelAttribute("danhMucList")
    public List<DanhMuc> getTop6DanhMucList() {
        return khoaHocService.getTop6DanhMucCoKhoaHoc();
    }

    @ModelAttribute("taiKhoan")
    public TaiKhoan getTaiKhoan(Authentication authentication) {
        return authService.getTaiKhoanFromAuth(authentication);
    }

    @ModelAttribute("tenNguoiDung")
    public String getTenNguoiDung(Authentication authentication) {
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        return (taiKhoan != null) ? taiKhoan.getName() : null;
    }

    @ModelAttribute("currentUri")
    public String getCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("likedCourseIds")
    public Set<Integer> getLikedCourseIds(Authentication authentication) {
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan != null) {
            return nguoiDungThichKhoaHocRepository
                    .findByTaiKhoan_TaikhoanId(taiKhoan.getTaikhoanId())
                    .stream()
                    .map(like -> like.getKhoaHoc().getKhoahocId())
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
