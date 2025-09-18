package com.duantn.controllers.controllerAdmin;

import com.duantn.entities.TaiKhoan;
import com.duantn.entities.VerificationToken;
import com.duantn.repositories.RoleRepository;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.repositories.VerificationTokenRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/quanly-nhanvien")
public class QuanLyNhanVienController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RoleRepository roleRepo;

    @GetMapping()
    public String hienThiFormThemNhanVien(Model model) {
        List<TaiKhoan> danhSachNhanVien = taiKhoanRepo.findByRole_Name("ROLE_NHANVIEN");
        model.addAttribute("danhSachNhanVien", danhSachNhanVien);
        return "views/gdienQuanLy/danhsachnhanvien";
    }

    @PostMapping("/them")
    public String themNhanVien(@RequestParam String email,
            @RequestParam String name,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (name == null || name.trim().length() < 2) {
            redirectAttributes.addFlashAttribute("error", "Tên phải có ít nhất 2 ký tự!");
            return "redirect:/admin/quanly-nhanvien";
        }

        if (taiKhoanRepo.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email này đã tồn tại!");
            return "redirect:/admin/quanly-nhanvien";
        }

        TaiKhoan tk = TaiKhoan.builder()
                .email(email)
                .name(name)
                .status(false)
                .role(roleRepo.findByName("ROLE_NHANVIEN").orElseThrow())
                .build();

        taiKhoanRepo.save(tk);

        String token = java.util.UUID.randomUUID().toString();
        VerificationToken vtoken = new VerificationToken(token, email);
        tokenRepo.save(vtoken);

        String link = "http://localhost:8080/xacthuc?token=" + token;
        String noiDung = "Chào " + name + ",\n\n"
                + "Chúc mừng bạn đã trở thành nhân viên của hệ thống quản lý khóa học GlobalEdu!\n"
                + "Chúng tôi rất vui khi được đồng hành và làm việc cùng bạn.\n\n"
                + "Vui lòng nhấn vào liên kết dưới đây để đặt mật khẩu và kích hoạt tài khoản của bạn:\n"
                + link + "\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ GlobalEdu";
        guiEmailXacThuc(email, "Xác thực tài khoản nhân viên", noiDung);

        redirectAttributes.addFlashAttribute("message", "Đã thêm nhân viên và gửi email xác thực..!");
        return "redirect:/admin/quanly-nhanvien";
    }

    private void guiEmailXacThuc(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    @PostMapping("/toggle-status-nhanvien/{id}")
    public String toggleStatus(@PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {
        TaiKhoan nhanVien = taiKhoanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        nhanVien.setStatus(!nhanVien.isStatus());
        taiKhoanRepo.save(nhanVien);

        redirectAttributes.addFlashAttribute("success", nhanVien.isStatus()
                ? "Tài khoản đã được mở khóa!"
                : "Tài khoản đã bị khóa!");

        return "redirect:/admin/quanly-nhanvien";
    }
}
