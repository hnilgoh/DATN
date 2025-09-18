package com.duantn.controllers.controllerHocVien;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.duantn.entities.KhoaHoc;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.KhoaHocService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class YeuThichKhoaHocController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @PostMapping("/khoaHoc/{id}/like")
    @ResponseBody
    public ResponseEntity<?> likeCourse(@PathVariable Integer id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập.");
        }

        return taiKhoanRepository.findByEmail(authentication.getName()).map(taiKhoan -> {
            boolean isLiked = khoaHocService.toggleLike(id, taiKhoan.getTaikhoanId());
            KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(id);

            if (khoaHoc != null) {
                Map<String, Object> response = Map.of("newLikeCount",
                        khoaHoc.getLuotThich() != null ? khoaHoc.getLuotThich() : 0, "isLiked",
                        isLiked);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        }).orElse(ResponseEntity.status(404).body("Không tìm thấy tài khoản."));
    }

    @PostMapping("/auth/save-redirect-url")
    @ResponseBody
    public ResponseEntity<Void> saveRedirectUrl(HttpServletRequest request, @RequestParam String redirect) {
        request.getSession().setAttribute("redirectAfterLogin", redirect);
        return ResponseEntity.ok().build();
    }

}
