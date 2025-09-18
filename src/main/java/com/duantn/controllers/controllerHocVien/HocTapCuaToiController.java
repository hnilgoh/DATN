package com.duantn.controllers.controllerHocVien;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duantn.entities.DangHoc;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.DangHocRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.KhoaHocService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HocTapCuaToiController {

    private final DangHocRepository dangHocRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final KhoaHocService khoaHocService;

    @GetMapping("/hoc-vien/hoc-tap")
    public String hocTapCuaToi(@RequestParam(name = "tab", defaultValue = "hoc-tap") String tab,
            Model model, Principal principal) {
        // Lấy tài khoản hiện tại
        String email = principal.getName();
        TaiKhoan taikhoan = taiKhoanRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với email: " + email));

        // Khóa học đang học
        List<DangHoc> dangHocList = dangHocRepository.findByTaikhoanIdWithKhoaHoc(taikhoan.getTaikhoanId());
        model.addAttribute("tatCaDangKy", dangHocList);

        // Chứng chỉ đã nhận
        List<DangHoc> daNhanChungChi = dangHocList.stream()
                .filter(DangHoc::isDaCap_ChungChi)
                .collect(Collectors.toList());
        model.addAttribute("chungChi", daNhanChungChi);

        // Khóa học yêu thích
        List<KhoaHoc> favoriteCourses = khoaHocService.findLikedCoursesByAccountId(taikhoan.getTaikhoanId());
        model.addAttribute("favoriteCourses", favoriteCourses);

        model.addAttribute("activeTab", tab);

        return "views/gdienHocVien/hoc-tap-cua-toi";
    }

    @PostMapping("/khoa-hoc/{id}/like")
    @ResponseBody
    public ResponseEntity<?> likeCourse(@PathVariable Integer id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập.");
        }

        return taiKhoanRepository.findByEmail(authentication.getName())
                .map(taiKhoan -> {
                    boolean isLiked = khoaHocService.toggleLike(id, taiKhoan.getTaikhoanId());
                    KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(id);

                    if (khoaHoc != null) {
                        Map<String, Object> response = Map.of(
                                "newLikeCount", khoaHoc.getLuotThich() != null ? khoaHoc.getLuotThich() : 0,
                                "isLiked", isLiked);
                        return ResponseEntity.ok(response);
                    }
                    return ResponseEntity.notFound().build();
                })
                .orElse(ResponseEntity.status(404).body("Không tìm thấy tài khoản."));
    }
}
