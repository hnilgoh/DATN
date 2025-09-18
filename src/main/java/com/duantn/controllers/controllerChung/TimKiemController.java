package com.duantn.controllers.controllerChung;

import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.services.KhoaHocService;
import com.duantn.services.DangHocService;
import com.duantn.services.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TimKiemController {

    private final KhoaHocService khoaHocService;
    private final DangHocService dangHocService;

    @GetMapping("/tim-kiem")
    public String timKiem(
            @RequestParam(value = "q", required = false) String query,
            Model model) {

        try {
            log.info("=== BẮT ĐẦU TÌM KIẾM KHÓA HỌC ===");
            log.info("Từ khóa: '{}'", query);

            List<KhoaHoc> ketQuaTimKiem = khoaHocService.timKiemTheoTenPublished(query);

            model.addAttribute("khoaHocList", khoaHocService.layKhoaHocTheoTrangThai(TrangThaiKhoaHoc.PUBLISHED));
            model.addAttribute("ketQuaTimKiem", ketQuaTimKiem);
            model.addAttribute("soLuongKetQua", ketQuaTimKiem.size());
            model.addAttribute("query", query); // để hiển thị lại từ khóa

            // Thêm thông tin về khóa học đã mua nếu người dùng đã đăng nhập
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getName().equals("anonymousUser") &&
                    authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                TaiKhoan taiKhoan = userDetails.getTaiKhoan();
                Set<Integer> enrolledCourseIds = getEnrolledCourseIds(taiKhoan.getTaikhoanId());
                model.addAttribute("enrolledCourseIds", enrolledCourseIds);
            }

            log.info("Tìm thấy {} kết quả", ketQuaTimKiem.size());
            log.info("=== KẾT THÚC TÌM KIẾM ===");

            return "views/gdienChung/tim-kiem";

        } catch (Exception e) {
            log.error("❌ Lỗi khi tìm kiếm: {}", e.getMessage(), e);
            model.addAttribute("error", "Có lỗi xảy ra khi tìm kiếm.");
            model.addAttribute("ketQuaTimKiem", List.of());
            model.addAttribute("soLuongKetQua", 0);
            model.addAttribute("khoaHocList", khoaHocService.layKhoaHocTheoTrangThai(TrangThaiKhoaHoc.PUBLISHED));
            return "views/gdienChung/tim-kiem";
        }
    }

    private Set<Integer> getEnrolledCourseIds(Integer taiKhoanId) {
        List<KhoaHoc> allCourses = khoaHocService.layKhoaHocTheoTrangThai(TrangThaiKhoaHoc.PUBLISHED);
        return allCourses.stream()
                .filter(course -> dangHocService.isEnrolled(taiKhoanId, course.getKhoahocId()))
                .map(KhoaHoc::getKhoahocId)
                .collect(Collectors.toSet());
    }

}