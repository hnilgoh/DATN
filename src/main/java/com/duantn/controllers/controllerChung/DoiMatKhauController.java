package com.duantn.controllers.controllerChung;

import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/doi-mat-khau")
public class DoiMatKhauController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showDoiMatKhauForm(Model model) {
        return "views/gdienChung/doiMatKhau";
    }

    @PostMapping
    public String doiMatKhau(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String email = authentication.getName(); // Lấy email đăng nhập
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(email).orElse(null);

        if (taiKhoan == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy tài khoản.");
            return "redirect:/doi-mat-khau";
        }

        if (!passwordEncoder.matches(currentPassword, taiKhoan.getPassword())) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu hiện tại không đúng.");
            return "redirect:/doi-mat-khau";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu mới và xác nhận không khớp.");
            return "redirect:/doi-mat-khau";
        }

        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return "redirect:/doi-mat-khau";
        }

        // Kiểm tra mật khẩu mạnh: ít nhất 1 chữ cái, 1 số, 1 ký tự đặc biệt
        if (!newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$")) {
            redirectAttributes.addFlashAttribute("message",
                    "Mật khẩu mới phải chứa ít nhất một chữ cái, một số và một ký tự đặc biệt.");
            return "redirect:/doi-mat-khau";
        }

        // Mã hóa và lưu mật khẩu mới
        taiKhoan.setPassword(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(taiKhoan);

        redirectAttributes.addFlashAttribute("message", "Bạn đã đổi mật khẩu thành công!");
        return "redirect:/doi-mat-khau";
    }
}
