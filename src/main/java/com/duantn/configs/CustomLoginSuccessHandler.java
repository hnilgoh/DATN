package com.duantn.configs;

import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.duantn.entities.TaiKhoan;
import com.duantn.services.CustomOAuth2User;
import com.duantn.services.CustomUserDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // ✅ Gán session để hiển thị modal chính sách chỉ 1 lần sau khi đăng nhập
        request.getSession().setAttribute("showPolicyPopup", true);

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomOAuth2User customOAuth2User) {
            TaiKhoan taiKhoan = customOAuth2User.getTaiKhoan();
            request.getSession().setAttribute("taiKhoan", taiKhoan);
        } else if (principal instanceof CustomUserDetails customUserDetails) {
            TaiKhoan taiKhoan = customUserDetails.getTaiKhoan();
            request.getSession().setAttribute("taiKhoan", taiKhoan);
        }

        // ✅ Lấy danh sách vai trò người dùng
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = null;

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            switch (role) {
                case "ROLE_ADMIN":
                    redirectUrl = "/admin";
                    break;

                case "ROLE_NHANVIEN":
                    redirectUrl = "/nhanvien";
                    break;

                case "ROLE_HOCVIEN":
                case "ROLE_GIANGVIEN":
                    String redirectParam = (String) request.getSession().getAttribute("redirectAfterLogin");
                    if (redirectParam != null && !redirectParam.isBlank()
                            && !redirectParam.contains("/admin") && !redirectParam.contains("/nhanvien")) {
                        redirectUrl = redirectParam;
                        request.getSession().removeAttribute("redirectAfterLogin");
                        break;
                    }

                    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                    if (savedRequest != null) {
                        String targetUrl = savedRequest.getRedirectUrl();

                        // ❌ Không cho redirect về /admin hoặc /nhanvien nếu không đủ quyền
                        if (!targetUrl.contains("/admin") && !targetUrl.contains("/nhanvien")) {
                            redirectUrl = targetUrl;
                            break;
                        }
                    }
                    // ✅ Nếu không có URL trước đó → chuyển về trang chủ
                    redirectUrl = "/";
                    break;

                default:
                    // ✅ Vai trò không xác định → về trang chủ
                    redirectUrl = "/";
            }

            if (redirectUrl != null) {
                break;
            }
        }

        // ✅ Phòng trường hợp không có role nào khớp
        if (redirectUrl == null) {
            redirectUrl = "/";
        }

        // ✅ Chuyển hướng đến URL phù hợp sau khi đăng nhập
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
