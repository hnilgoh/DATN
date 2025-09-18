package com.duantn.controllers.controllerChung;

import com.duantn.dtos.DangKyHocVienDto;
import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.entities.VerificationToken;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.TokenService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class DangKyController {

    private final TaiKhoanRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/dangky")
    public String showRegistrationForm(Model model, HttpSession session) {
        model.addAttribute("user", new DangKyHocVienDto());
        return "views/gdienChung/dangky";
    }

    @PostMapping("/dangky")
    public String registerUser(@ModelAttribute("user") @Valid DangKyHocVienDto dto,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors())
            return "views/gdienChung/dangky";

        if (accountRepository.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "email.exists", "Email đã được sử dụng");
            return "views/gdienChung/dangky";
        }

        session.setAttribute("pendingUser", dto);
        tokenService.generateAndSendToken(dto.getEmail(), dto.getName(), "Xác minh tài khoản",
                "Mã xác minh của bạn là:");
        return "redirect:/auth/verify?type=register";
    }

    @GetMapping("/verify")
    public String showVerifyForm(Model model, @RequestParam(value = "type", defaultValue = "register") String type) {
        model.addAttribute("type", type);
        return "views/gdienChung/verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam("code") String code,
            @RequestParam(value = "type", required = false) String type,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Optional<VerificationToken> tokenOpt = tokenService.verifyToken(code);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Mã xác minh không đúng.");
            model.addAttribute("type", type != null ? type : "register");
            return "views/gdienChung/verify";
        }

        VerificationToken token = tokenOpt.get();

        // Kiểm tra hạn sử dụng mã
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            tokenService.delete(token); // xoá token đã hết hạn
            model.addAttribute("error", "Mã xác minh đã hết hạn.");
            model.addAttribute("type", type != null ? type : "register");
            return "views/gdienChung/verify";
        }

        // ✅ Xử lý xác minh đăng ký
        DangKyHocVienDto pending = (DangKyHocVienDto) session.getAttribute("pendingUser");
        if (pending != null && pending.getEmail().equals(token.getEmail())) {
            Role studentRole = roleRepository.findByName("ROLE_HOCVIEN")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

            TaiKhoan account = TaiKhoan.builder()
                    .name(pending.getName())
                    .email(pending.getEmail())
                    .phone(pending.getPhone())
                    .password(passwordEncoder.encode(pending.getPassword()))
                    .status(true)
                    .role(studentRole)
                    .build();

            accountRepository.save(account);

            // UsernamePasswordAuthenticationToken authToken = new
            // UsernamePasswordAuthenticationToken(
            // account, null, List.of(new
            // SimpleGrantedAuthority(account.getRole().getName())));

            // SecurityContextHolder.getContext().setAuthentication(authToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(account.getEmail());
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            session.setAttribute("currentUser", account);

            tokenService.delete(token);
            session.removeAttribute("pendingUser");

            return "redirect:/";
        }

        // Xử lý xác minh cập nhật email
        String pendingEmailUpdate = (String) session.getAttribute("pendingEmailUpdate");
        String oldEmail = (String) session.getAttribute("currentEmail");

        if (pendingEmailUpdate != null && oldEmail != null && token.getEmail().equals(pendingEmailUpdate)) {
            TaiKhoan taiKhoan = accountRepository.findByEmail(oldEmail).orElse(null);
            if (taiKhoan != null) {
                taiKhoan.setEmail(pendingEmailUpdate);
                accountRepository.save(taiKhoan);

                session.removeAttribute("pendingEmailUpdate");
                session.removeAttribute("currentEmail");
                tokenService.delete(token);

                UserDetails newDetails = userDetailsService.loadUserByUsername(pendingEmailUpdate);
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        newDetails, newDetails.getPassword(), newDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);

                redirectAttributes.addFlashAttribute("message", "Cập nhật email thành công.");
                redirectAttributes.addFlashAttribute("tab", "tab-email");
                return "redirect:/tai-khoan";
            }
        }

        model.addAttribute("error", "Phiên xác minh không hợp lệ.");
        model.addAttribute("type", type != null ? type : "register");
        return "views/gdienChung/verify";
    }

    // Gửi lại mã xác minh khi đăng ký
    @PostMapping("/resend-register")
    public String resendRegister(HttpSession session, RedirectAttributes redirectAttributes) {
        DangKyHocVienDto dto = (DangKyHocVienDto) session.getAttribute("pendingUser");
        if (dto == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đăng ký không hợp lệ.");
            return "redirect:/auth/dangky";
        }

        tokenService.generateAndSendToken(dto.getEmail(), dto.getName(), "Xác minh tài khoản",
                "Mã xác minh của bạn là:");
        return "redirect:/auth/verify?type=register";
    }

    // Gửi lại mã xác minh khi cập nhật email
    @PostMapping("/resend-update-email")
    public String resendUpdateEmail(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("pendingEmailUpdate");
        String name = (String) session.getAttribute("currentUserName"); // giả sử đã lưu tên

        if (email == null || name == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên xác minh không hợp lệ.");
            return "redirect:/tai-khoan";
        }

        tokenService.generateAndSendToken(email, name, "Xác minh cập nhật email", "Mã xác minh của bạn là:");
        return "redirect:/auth/verify?type=update-email";
    }

}