package com.duantn.controllers.controllerChung;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.duantn.entities.TaiKhoan;
import com.duantn.entities.VerificationToken;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import jakarta.mail.internet.MimeMessage;

@Controller
@RequestMapping("/lay-lai-mat-khau")
public class QuenMatKhauController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping()
    public String showEmailForm() {
        return "views/gdienChung/quenmatkhau";
    }

    @PostMapping()
    public String handleEmail(@RequestParam("email") String email, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByEmail(email);
        if (tkOpt.isEmpty()) {
            model.addAttribute("error", "Email không tồn tại trong hệ thống.");
            return "views/gdienChung/quenmatkhau";
        }

        // Xóa token cũ nếu có
        tokenRepository.findByEmail(email).ifPresent(tokenRepository::delete);

        // Tạo mã xác minh mới
        String code = String.format("%06d", new Random().nextInt(999999));
        VerificationToken token = new VerificationToken();
        token.setEmail(email);
        token.setToken(code);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(2));
        tokenRepository.save(token);

        // Gửi email xác minh
        sendVerificationEmail(email, code);

        // Lưu email vào session
        session.setAttribute("resetEmail", email);

        redirectAttributes.addFlashAttribute("resetVerify", true);
        return "redirect:/lay-lai-mat-khau/verify";
    }

    @GetMapping("/verify")
    public String showVerifyForm(Model model, HttpSession session,
            @ModelAttribute("resetVerify") Object resetVerifyAttr) {
        model.addAttribute("type", "forgot");
        model.addAttribute("email", session.getAttribute("resetEmail")); // để hidden input có giá trị

        if (resetVerifyAttr != null) {
            model.addAttribute("resetVerify", true); // ✅ Truyền lại vào model để JS sử dụng
        }

        return "views/gdienChung/verify";
    }

    @PostMapping("/verify")
    public String handleVerify(@RequestParam("code") String code,
            @RequestParam("email") String email, // lấy từ hidden input
            HttpSession session, Model model) {

        if (email == null || email.isEmpty()) {
            model.addAttribute("error", "Phiên làm việc không hợp lệ.");
            return "views/gdienChung/quenmatkhau";
        }

        Optional<VerificationToken> tokenOpt = tokenRepository.findByEmailAndToken(email, code);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Mã xác minh không đúng.");
            model.addAttribute("type", "forgot");
            model.addAttribute("email", email);
            return "views/gdienChung/verify";
        }

        VerificationToken token = tokenOpt.get();

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            // KHÔNG remove session.resetEmail — vẫn giữ để tiếp tục gửi lại mã
            model.addAttribute("error", "Mã xác minh đã hết hạn. Vui lòng gửi lại mã xác thực.");
            model.addAttribute("type", "forgot");
            model.addAttribute("email", email);
            return "views/gdienChung/verify"; // ✅ Vẫn ở trang xác minh
        }

        session.setAttribute("verifiedEmail", email);
        tokenRepository.delete(token);
        return "redirect:/lay-lai-mat-khau/reset";
    }

    @GetMapping("/reset")
    public String showResetForm() {
        return "views/gdienChung/buoc2quenmatkhau";
    }

    @PostMapping("/reset")
    public String handleReset(@RequestParam("password") String password,
            @RequestParam("confirm") String confirm,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        String email = (String) session.getAttribute("verifiedEmail");

        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên làm việc không hợp lệ.");
            return "redirect:/lay-lai-mat-khau/reset";
        }

        if (!password.equals(confirm)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận không khớp.");
            return "redirect:/lay-lai-mat-khau/reset";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            return "redirect:/lay-lai-mat-khau/reset";
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$")) {
            redirectAttributes.addFlashAttribute("error",
                    "Mật khẩu phải chứa ít nhất một chữ cái, một số và một ký tự đặc biệt.");
            return "redirect:/lay-lai-mat-khau/reset";
        }

        TaiKhoan tk = taiKhoanRepository.findByEmail(email).orElse(null);
        if (tk == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/lay-lai-mat-khau/reset";
        }

        tk.setPassword(passwordEncoder.encode(password));
        taiKhoanRepository.save(tk);

        session.removeAttribute("verifiedEmail");
        session.removeAttribute("resetEmail");

        model.addAttribute("message", "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập lại.");
        return "views/gdienChung/buoc2quenmatkhau";
    }

    private void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Xác minh đặt lại mật khẩu - GlobalEdu");
            helper.setFrom("globaledu237@gmail.com", "GlobalEdu");

            String html = String.format(
                    """
                            <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                                <h2>Xin chào,</h2>
                                <p>Bạn vừa yêu cầu đặt lại mật khẩu tại <strong>GlobalEdu</strong>.</p>
                                <p>Mã xác minh của bạn là:</p>
                                <div style="font-size: 24px; font-weight: bold; color: #2c3e50; text-align: center; margin: 20px 0;">%s</div>
                                <p>Mã xác minh sẽ hết hạn sau <strong>2 phút</strong>. Vui lòng không chia sẻ mã này với người khác.</p>
                                <p>Nếu bạn không thực hiện hành động này, vui lòng bỏ qua email này.</p>
                                <br>
                                <p>Trân trọng,<br><strong>Đội ngũ GlobalEdu</strong></p>
                                <hr>
                                <p style="font-size: 12px; color: gray;">Email này được gửi tự động. Vui lòng không phản hồi.</p>
                            </div>
                            """,
                    code);

            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email xác minh: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
