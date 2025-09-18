package com.duantn.controllers.controllerAdmin;

import com.duantn.entities.TaiKhoan;
import com.duantn.entities.VerificationToken;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class XacThucController {

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/xacthuc")
    public String hienThiFormDatMatKhau(@RequestParam("token") String token, Model model) {
        VerificationToken vtoken = tokenRepo.findByToken(token)
                .orElse(null);

        if (vtoken == null || vtoken.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Liên kết không hợp lệ hoặc đã hết hạn.");
            return "views/gdienQuanLy/nhan-vien-xac-thuc";
        }

        model.addAttribute("token", token);
        return "views/gdienQuanLy/nhan-vien-xac-thuc";
    }

    @PostMapping("/xacthuc")
    public String xuLyDatMatKhau(@RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        VerificationToken vtoken = tokenRepo.findByToken(token)
                .orElse(null);

        if (vtoken == null || vtoken.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Liên kết không hợp lệ hoặc đã hết hạn.");
            return "views/gdienQuanLy/nhan-vien-xac-thuc";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("message", "Mật khẩu và xác nhận mật khẩu không trùng khớp.");
            return "views/gdienQuanLy/nhan-vien-xac-thuc";
        }

        TaiKhoan tk = taiKhoanRepo.findByEmail(vtoken.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        tk.setPassword(passwordEncoder.encode(password));
        tk.setStatus(true);
        taiKhoanRepo.save(tk);
        tokenRepo.delete(vtoken);

        model.addAttribute("token", token);
        model.addAttribute("message", "Thiết lập mật khẩu thành công! Bạn có thể đăng nhập.");
        return "views/gdienQuanLy/nhan-vien-xac-thuc";
    }
}
