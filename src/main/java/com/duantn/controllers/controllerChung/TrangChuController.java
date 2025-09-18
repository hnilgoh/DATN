package com.duantn.controllers.controllerChung;

import com.duantn.entities.DanhMuc;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.DangHocService;
import com.duantn.services.KhoaHocService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TrangChuController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private DangHocService dangHocService;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model, @ModelAttribute("taiKhoan") TaiKhoan taiKhoan,
            @RequestParam(defaultValue = "0") int page) {
        boolean isHocVien = request.isUserInRole("ROLE_HOCVIEN");
        boolean isGiangVien = request.isUserInRole("ROLE_GIANGVIEN");
        boolean isAdmin = request.isUserInRole("ROLE_ADMIN");
        boolean isNhanVien = request.isUserInRole("ROLE_NHANVIEN");

        if (isAdmin || isNhanVien) {
            return "redirect:/auth/dangnhap?error=unauthorized";
        }

        Object popupFlag = request.getSession().getAttribute("showPolicyPopup");
        if (popupFlag != null && popupFlag.equals(true)) {
            model.addAttribute("showPolicyPopup", true);
            request.getSession().removeAttribute("showPolicyPopup");
        }

        page = Math.max(page, 0);
        int pageSize = 8;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<KhoaHoc> allCourses = khoaHocService.getTatCaKhoaHocDaXuatBan();

        if ((isHocVien || isGiangVien) && taiKhoan != null) {
            allCourses = locKhoaHocKhongThuocNguoiDung(allCourses, taiKhoan, isGiangVien);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCourses.size());
        Page<KhoaHoc> khoaHocPage = new PageImpl<>(allCourses.subList(start, end), pageable, allCourses.size());

        // Tính khoảng trang hiển thị
        int currentPage = khoaHocPage.getNumber();
        int totalPages = khoaHocPage.getTotalPages();
        int startPage = Math.max(0, currentPage - 1);
        int endPage = Math.min(totalPages - 1, currentPage + 1);

        boolean showPagination = allCourses.size() > pageSize;

        model.addAttribute("khoaHocPage", khoaHocPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showPagination", showPagination);
        model.addAttribute("khoaHocTheoDanhMuc", getKhoaHocTheoDanhMuc(taiKhoan, isGiangVien));

        if (isHocVien && taiKhoan != null) {
            Set<Integer> enrolledCourseIds = getEnrolledCourseIds(taiKhoan.getTaikhoanId());
            List<KhoaHoc> enrolledCourses = getEnrolledCourses(taiKhoan.getTaikhoanId());
            model.addAttribute("enrolledCourseIds", enrolledCourseIds);
            model.addAttribute("enrolledCourses", enrolledCourses);
        }

        if (isHocVien || isGiangVien) {
            return "views/gdienHocVien/home-hocvien";
        } else {
            model.addAttribute("khoaHocList", khoaHocPage);
            return "views/gdienChung/home";
        }
    }

    private Map<Integer, List<KhoaHoc>> getKhoaHocTheoDanhMuc(TaiKhoan taiKhoan, boolean isGiangVien) {
        Map<Integer, List<KhoaHoc>> khoaHocTheoDanhMuc = new HashMap<>();
        Set<Integer> khoaHocDaHocIds = taiKhoan != null ? getEnrolledCourseIds(taiKhoan.getTaikhoanId()) : Set.of();
        Set<Integer> khoaHocTuTaoIds = (isGiangVien && taiKhoan != null && taiKhoan.getGiangVien() != null)
                ? khoaHocService.getKhoaHocByGiangVien(taiKhoan.getGiangVien().getGiangvienId())
                        .stream().map(KhoaHoc::getKhoahocId).collect(Collectors.toSet())
                : Set.of();

        for (DanhMuc dm : khoaHocService.getDanhMucCoKhoaHoc()) {
            List<KhoaHoc> ds = khoaHocService.getKhoaHocTheoDanhMuc(dm.getDanhmucId());
            List<KhoaHoc> dsLoc = ds.stream()
                    .filter(kh -> !khoaHocDaHocIds.contains(kh.getKhoahocId()))
                    .filter(kh -> !khoaHocTuTaoIds.contains(kh.getKhoahocId()))
                    .collect(Collectors.toList());
            khoaHocTheoDanhMuc.put(dm.getDanhmucId(), dsLoc);
        }

        return khoaHocTheoDanhMuc;
    }

    private Set<Integer> getEnrolledCourseIds(Integer taiKhoanId) {
        return khoaHocService.getTatCaKhoaHocDaXuatBan().stream()
                .filter(course -> dangHocService.isEnrolled(taiKhoanId, course.getKhoahocId()))
                .map(KhoaHoc::getKhoahocId)
                .collect(Collectors.toSet());
    }

    private List<KhoaHoc> getEnrolledCourses(Integer taiKhoanId) {
        return khoaHocService.getTatCaKhoaHocDaXuatBan().stream()
                .filter(course -> dangHocService.isEnrolled(taiKhoanId, course.getKhoahocId()))
                .collect(Collectors.toList());
    }

    private List<KhoaHoc> locKhoaHocKhongThuocNguoiDung(List<KhoaHoc> allCourses, TaiKhoan taiKhoan,
            boolean isGiangVien) {
        Set<Integer> khoaHocDaHocIds = getEnrolledCourseIds(taiKhoan.getTaikhoanId());
        Set<Integer> khoaHocTuTaoIds = (isGiangVien && taiKhoan.getGiangVien() != null)
                ? khoaHocService.getKhoaHocByGiangVien(taiKhoan.getGiangVien().getGiangvienId())
                        .stream().map(KhoaHoc::getKhoahocId).collect(Collectors.toSet())
                : Set.of();

        return allCourses.stream()
                .filter(kh -> !khoaHocDaHocIds.contains(kh.getKhoahocId()))
                .filter(kh -> !khoaHocTuTaoIds.contains(kh.getKhoahocId()))
                .collect(Collectors.toList());
    }

    @RequestMapping("/khoahoc/phantrang")
    public String loadMoreCourses(HttpServletRequest request, Model model,
            @ModelAttribute("taiKhoan") TaiKhoan taiKhoan,
            @RequestParam(defaultValue = "0") int page) {

        page = Math.max(page, 0);

        boolean isGiangVien = request.isUserInRole("ROLE_GIANGVIEN");
        boolean isHocVien = request.isUserInRole("ROLE_HOCVIEN");

        int pageSize = 8;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<KhoaHoc> allCourses = khoaHocService.getTatCaKhoaHocDaXuatBan();

        if ((isHocVien || isGiangVien) && taiKhoan != null) {
            allCourses = locKhoaHocKhongThuocNguoiDung(allCourses, taiKhoan, isGiangVien);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allCourses.size());
        Page<KhoaHoc> khoaHocPage = new PageImpl<>(allCourses.subList(start, end), pageable, allCourses.size());

        int currentPage = khoaHocPage.getNumber();
        int totalPages = khoaHocPage.getTotalPages();
        int startPage = Math.max(0, currentPage - 1);
        int endPage = Math.min(totalPages - 1, currentPage + 1);

        model.addAttribute("khoaHocPage", khoaHocPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "views/fragments/khoahoc-list :: fragment";
    }
}