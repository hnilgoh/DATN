package com.duantn.services;

import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    public TaiKhoan getTaiKhoanFromAuth(Authentication authentication) {
        if (authentication == null)
            return null;

        Object principal = authentication.getPrincipal();
        Integer id = null;

        if (principal instanceof CustomUserDetails userDetails) {
            id = userDetails.getTaiKhoan().getTaikhoanId();
        } else if (principal instanceof CustomOAuth2User oauth2User) {
            id = oauth2User.getTaiKhoan().getTaikhoanId();
        } else if (principal instanceof DefaultOAuth2User defaultOAuth2User) {
            String email = defaultOAuth2User.getAttribute("email");
            TaiKhoan tk = taiKhoanRepository.findByEmail(email).orElse(null);
            if (tk != null) {
                id = tk.getTaikhoanId();
            }
        }

        // Luôn lấy bản mới nhất từ DB
        return (id != null) ? taiKhoanRepository.findById(id).orElse(null) : null;
    }
}
