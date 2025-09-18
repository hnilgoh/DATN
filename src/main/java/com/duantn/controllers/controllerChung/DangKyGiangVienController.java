package com.duantn.controllers.controllerChung;

import com.duantn.dtos.DangKyGiangVienDto;
import com.duantn.dtos.GiangVienRegistrationDto;
import com.duantn.entities.GiangVien;
import com.duantn.entities.Role;
import com.duantn.entities.TaiKhoan;
import com.duantn.entities.VerificationToken;
import com.duantn.repositories.GiangVienRepository;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.repositories.VerificationTokenRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/dang-ky-giang-vien")
@RequiredArgsConstructor
public class DangKyGiangVienController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final JavaMailSender mailSender;
    private final RoleRepository roleRepository;
    private final GiangVienRepository giangVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;

    @GetMapping
    public String showEmailForm() {
        return "views/gdienChung/dangkygiangvien";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<TaiKhoan> existingAccountOpt = taiKhoanRepository.findByEmail(email);

        if (existingAccountOpt.isPresent()) {
            String roleName = existingAccountOpt.get().getRole().getName();
            if ("ROLE_GIANGVIEN".equals(roleName)) {
                redirectAttributes.addFlashAttribute("error", "Email này đã được đăng ký làm giảng viên.");
                return "redirect:/dang-ky-giang-vien";
            }
            if ("ROLE_ADMIN".equals(roleName) || "ROLE_NHANVIEN".equals(roleName)) {
                redirectAttributes.addFlashAttribute("error",
                        "Tài khoản có vai trò Quản trị viên hoặc Nhân viên không thể đăng ký làm giảng viên.");
                return "redirect:/dang-ky-giang-vien";
            }
        }

        try {
            String otp = String.format("%06d", new Random().nextInt(999999));

            // Xóa token cũ nếu có
            tokenRepository.findByEmail(email).ifPresent(tokenRepository::delete);
            // Tạo token mới
            VerificationToken token = new VerificationToken();
            token.setEmail(email);
            token.setToken(otp);
            token.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // OTP hết hạn sau 5 phút
            tokenRepository.save(token);

            sendVerificationEmail(email, otp);

            session.setAttribute("emailForOtp", email); // Lưu email vào session để gửi lại

            return "redirect:/dang-ky-giang-vien/verify-otp";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể gửi email. Vui lòng thử lại.");
            return "redirect:/dang-ky-giang-vien";
        }
    }

    private void sendVerificationEmail(String email, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Mã Xác Thực Đăng Ký Giảng Viên - GlobalEdu");
            helper.setFrom("globaledu237@gmail.com", "GlobalEdu");

            String html = String.format(
                    """
                            <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                                <h2>Chào bạn,</h2>
                                <p>Cảm ơn bạn đã bắt đầu quy trình đăng ký giảng viên tại <strong>GlobalEdu</strong>.</p>
                                <p>Mã xác minh của bạn là:</p>
                                <div style="font-size: 24px; font-weight: bold; color: #2c3e50; text-align: center; margin: 20px 0;">%s</div>
                                <p>Mã xác minh sẽ hết hạn sau <strong>5 phút</strong>. Vui lòng không chia sẻ mã này với người khác.</p>
                                <p>Trân trọng,<br><strong>Đội ngũ GlobalEdu</strong></p>
                            </div>
                            """,
                    otp);

            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            // Cân nhắc log lỗi ở đây
            throw new RuntimeException("Lỗi khi gửi email xác minh: " + e.getMessage());
        }
    }

    @GetMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("emailForOtp");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đã hết hạn, vui lòng bắt đầu lại.");
            return "redirect:/dang-ky-giang-vien";
        }

        try {
            String otp = String.format("%06d", new Random().nextInt(999999));

            tokenRepository.findByEmail(email).ifPresent(tokenRepository::delete);

            VerificationToken token = new VerificationToken();
            token.setEmail(email);
            token.setToken(otp);
            token.setExpiryTime(LocalDateTime.now().plusMinutes(5));
            tokenRepository.save(token);

            sendVerificationEmail(email, otp);

            redirectAttributes.addFlashAttribute("success", "Mã OTP mới đã được gửi đến email của bạn.");
            return "redirect:/dang-ky-giang-vien/verify-otp";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể gửi lại mã. Vui lòng thử lại sau.");
            return "redirect:/dang-ky-giang-vien/verify-otp";
        }
    }

    @GetMapping("/verify-otp")
    public String showOtpForm() {
        return "views/gdienChung/xacminhemailgiangvien";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, RedirectAttributes redirectAttributes,
            Model model) {
        Optional<VerificationToken> tokenOpt = tokenRepository.findByToken(otp);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Mã OTP không chính xác.");
            return "views/gdienChung/xacminhemailgiangvien";
        }

        VerificationToken token = tokenOpt.get();
        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã OTP đã hết hạn.");
            tokenRepository.delete(token);
            return "views/gdienChung/xacminhemailgiangvien";
        }

        String email = token.getEmail();
        session.setAttribute("registrationEmail", email);

        tokenRepository.delete(token);

        Optional<TaiKhoan> existingAccount = taiKhoanRepository.findByEmail(email);
        if (existingAccount.isEmpty()) {
            return "redirect:/dang-ky-giang-vien/register-new";
        } else {
            return "redirect:/dang-ky-giang-vien/upgrade-account";
        }
    }

    @GetMapping("/register-new")
    public String showNewInstructorForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("registrationEmail");
        if (email == null) {
            return "redirect:/dang-ky-giang-vien";
        }
        GiangVienRegistrationDto dto = new GiangVienRegistrationDto();
        dto.setEmail(email);
        model.addAttribute("giangVienDto", dto);
        return "views/gdienChung/hoantatdangkygiangvien";
    }

    @PostMapping("/register-new")
    public String processNewInstructorRegistration(@Valid @ModelAttribute("giangVienDto") GiangVienRegistrationDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (result.hasErrors()) {
            return "views/gdienChung/hoantatdangkygiangvien";
        }

        // Ràng buộc độ tuổi >= 18
        if (dto.getNgaySinh() != null) {
            int tuoi = LocalDateTime.now().getYear() - dto.getNgaySinh().getYear();
            if (tuoi < 18) {
                result.rejectValue("ngaySinh", "error.giangVienDto", "Bạn phải đủ 18 tuổi để đăng ký.");
                return "views/gdienChung/hoantatdangkygiangvien";
            }
        }

        try {
            // Tạo TaiKhoan
            Role instructorRole = roleRepository.findByName("ROLE_GIANGVIEN")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            TaiKhoan newAccount = new TaiKhoan();
            newAccount.setEmail(dto.getEmail());
            newAccount.setName(dto.getHoTen());
            newAccount.setPhone(dto.getSdt());
            newAccount.setPassword(passwordEncoder.encode(dto.getPassword()));
            newAccount.setRole(instructorRole);
            newAccount.setStatus(true); // Active by default

            TaiKhoan savedAccount = taiKhoanRepository.save(newAccount);

            UserDetails userDetails = userDetailsService.loadUserByUsername(savedAccount.getEmail());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            session.setAttribute("currentUser", savedAccount);

            // Tạo GiangVien
            GiangVien newInstructor = new GiangVien();
            newInstructor.setTaikhoan(savedAccount);
            newInstructor.setKinhNghiem(dto.getKinhNghiem());
            newInstructor.setKyNang(dto.getKyNang());
            newInstructor.setCCCD(dto.getCCCD());
            newInstructor.setCongViec(dto.getCongViec());
            newInstructor.setNgaySinh(dto.getNgaySinh());
            newInstructor.setGioiTinh(dto.getGioiTinh());
            newInstructor.setChuyenNganh(dto.getChuyenNganh());
            giangVienRepository.save(newInstructor);

            return "redirect:/giangvien/trang-giang-vien";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình đăng ký.");
            return "redirect:/dang-ky-giang-vien/register-new";
        }
    }

    @GetMapping("/upgrade-account")
    public String showUpgradeForm(HttpSession session) {
        String email = (String) session.getAttribute("registrationEmail");
        if (email == null) {
            return "redirect:/dang-ky-giang-vien";
        }
        return "views/gdienChung/xacnhannangcap";
    }

    @GetMapping("/upgrade-details")
    public String showUpgradeDetailsForm(Model model, HttpSession session) {
        String email = (String) session.getAttribute("registrationEmail");
        if (email == null) {
            return "redirect:/dang-ky-giang-vien";
        }
        model.addAttribute("giangVienDto", new DangKyGiangVienDto());
        return "views/gdienChung/thongtinnangcapchitiet";
    }

    @PostMapping("/upgrade-details")
    public String processUpgradeDetails(@Valid @ModelAttribute("giangVienDto") DangKyGiangVienDto dto,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "views/gdienChung/thongtinnangcapchitiet";
        }

        // 👉 THÊM RÀNG BUỘC TUỔI ≥ 18
        if (dto.getNgaySinh() != null) {
            int tuoi = LocalDateTime.now().getYear() - dto.getNgaySinh().getYear();
            if (tuoi < 18) {
                result.rejectValue("ngaySinh", "error.ngaySinh", "Bạn phải đủ 18 tuổi để tiếp tục.");
                return "views/gdienChung/thongtinnangcapchitiet";
            }
        }

        String email = (String) session.getAttribute("registrationEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đã hết hạn. Vui lòng thử lại.");
            return "redirect:/dang-ky-giang-vien";
        }

        session.setAttribute("instructorDetails", dto);

        return "redirect:/dang-ky-giang-vien/verify-password";
    }

    @GetMapping("/verify-password")
    public String showVerifyPasswordForm(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("registrationEmail");
        if (email == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đã hết hạn. Vui lòng thử lại.");
            return "redirect:/dang-ky-giang-vien";
        }
        return "views/gdienChung/xacnhanmatkhaugiangvien";
    }

    @PostMapping("/verify-password")
    public String processVerifyPassword(@RequestParam("password") String password,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("registrationEmail");
        DangKyGiangVienDto instructorDetails = (DangKyGiangVienDto) session.getAttribute("instructorDetails");

        if (email == null || instructorDetails == null) {
            redirectAttributes.addFlashAttribute("error", "Phiên đã hết hạn. Vui lòng thử lại.");
            return "redirect:/dang-ky-giang-vien";
        }

        Optional<TaiKhoan> accountOpt = taiKhoanRepository.findByEmail(email);
        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không tồn tại.");
            return "redirect:/dang-ky-giang-vien";
        }

        TaiKhoan account = accountOpt.get();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu không chính xác.");
            return "redirect:/dang-ky-giang-vien/verify-password";
        }

        try {
            // Cập nhật role
            Role instructorRole = roleRepository.findByName("ROLE_GIANGVIEN")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            account.setRole(instructorRole);

            taiKhoanRepository.save(account);

            UserDetails userDetails = userDetailsService.loadUserByUsername(account.getEmail());
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            // Tìm hoặc tạo mới thông tin giảng viên
            GiangVien instructor = giangVienRepository.findByTaikhoan(account)
                    .orElse(new GiangVien());

            instructor.setTaikhoan(account);
            instructor.setKinhNghiem(instructorDetails.getKinhNghiem());
            instructor.setKyNang(instructorDetails.getKyNang());
            instructor.setCCCD(instructorDetails.getCCCD());
            instructor.setCongViec(instructorDetails.getCongViec());
            instructor.setNgaySinh(instructorDetails.getNgaySinh());
            instructor.setGioiTinh(instructorDetails.getGioiTinh());
            instructor.setChuyenNganh(instructorDetails.getChuyenNganh());
            giangVienRepository.save(instructor);

            // Xóa các thuộc tính session không cần thiết
            session.removeAttribute("registrationEmail");
            session.removeAttribute("instructorDetails");
            session.removeAttribute("emailForOtp");

            redirectAttributes.addFlashAttribute("success", "Nâng cấp tài khoản thành công!");
            return "redirect:/giangvien/trang-giang-vien"; // Trang chính hoặc trang dành cho giảng viên

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình nâng cấp: " + e.getMessage());
            return "redirect:/dang-ky-giang-vien/verify-password";
        }
    }

}