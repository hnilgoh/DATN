package com.duantn.controllers.controllerHocVien;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duantn.dtos.TaiKhoanEmailDto;
import com.duantn.dtos.TaiKhoanPasswordDto;
import com.duantn.dtos.TaiKhoanUpdateDto;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.TaiKhoanRepository;
import com.duantn.services.CloudinaryService;
import com.duantn.services.TokenService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ThongTinCaNhanController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    private CloudinaryService cloudinaryService;

    private void addCommonModel(Model model, TaiKhoan tk,
            TaiKhoanUpdateDto updateDto,
            TaiKhoanEmailDto emailDto,
            TaiKhoanPasswordDto passwordDto) {
        model.addAttribute("taiKhoan", tk);
        model.addAttribute("taiKhoanUpdateDto", updateDto);
        model.addAttribute("emailDto", emailDto);
        model.addAttribute("passwordDto", passwordDto);
    }

    @GetMapping("/tai-khoan")
    public String hienThiTaiKhoan(Model model, Authentication authentication,
            @RequestParam(value = "tab", required = false) String tab) {
        String email = authentication.getName();
        TaiKhoan tk = taiKhoanRepository.findByEmail(email).orElse(null);

        if (!model.containsAttribute("taiKhoanUpdateDto")) {
            TaiKhoanUpdateDto updateDto = new TaiKhoanUpdateDto();
            updateDto.setName(tk.getName());
            updateDto.setPhone(tk.getPhone());
            model.addAttribute("taiKhoanUpdateDto", updateDto);
        }

        if (!model.containsAttribute("emailDto"))
            model.addAttribute("emailDto", new TaiKhoanEmailDto());

        if (!model.containsAttribute("passwordDto"))
            model.addAttribute("passwordDto", new TaiKhoanPasswordDto());

        model.addAttribute("taiKhoan", tk);

        if (tab != null) {
            model.addAttribute("tab", tab);
        }
        return "views/gdienHocVien/tai-khoan";
    }

    @PostMapping("/tai-khoan/cap-nhat-avatar")
    public String capNhatAvatar(@RequestParam("avatar") MultipartFile avatarFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        TaiKhoan tk = taiKhoanRepository.findByEmail(authentication.getName()).orElse(null);
        if (tk == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy tài khoản.");
            return "redirect:/tai-khoan";
        }

        if (!avatarFile.isEmpty()) {
            try {
                String avatarUrl = tk.getAvatar();
                if (avatarUrl != null && !avatarUrl.isBlank() && !avatarUrl.contains("default-avatar")) {
                    String publicId = cloudinaryService.extractPublicIdFromUrl(avatarUrl);
                    if (publicId != null)
                        cloudinaryService.deleteImage(publicId);
                }
                String imageUrl = cloudinaryService.uploadImage(avatarFile);
                tk.setAvatar(imageUrl);
                taiKhoanRepository.save(tk);
                redirectAttributes.addFlashAttribute("message", "Cập nhật ảnh đại diện thành công.");
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Lỗi khi tải ảnh lên Cloudinary.");
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn một tệp ảnh.");
        }
        redirectAttributes.addFlashAttribute("message", "Cập nhật ảnh đại diện thành công.");
        redirectAttributes.addFlashAttribute("tab", "tab-avatar");
        return "redirect:/tai-khoan";
    }

    @PostMapping("/tai-khoan/cap-nhat")
    public String capNhatThongTin(
            @Valid @ModelAttribute("taiKhoanUpdateDto") TaiKhoanUpdateDto dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        TaiKhoan tk = taiKhoanRepository.findByEmail(authentication.getName()).orElse(null);
        if (tk == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy tài khoản.");
            return "redirect:/tai-khoan";
        }

        if (result.hasErrors()) {
            addCommonModel(model, tk, dto, new TaiKhoanEmailDto(), new TaiKhoanPasswordDto());
            return "views/gdienHocVien/tai-khoan";
        }

        tk.setName(dto.getName());
        tk.setPhone(dto.getPhone());
        taiKhoanRepository.save(tk);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công.");
        redirectAttributes.addFlashAttribute("tab", "tab-thongtin");
        return "redirect:/tai-khoan";
    }

    @PostMapping("/tai-khoan/cap-nhat-email")
    public String capNhatEmail(
            @Valid @ModelAttribute("emailDto") TaiKhoanEmailDto dto,
            BindingResult result,
            Authentication authentication,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        TaiKhoan tk = taiKhoanRepository.findByEmail(authentication.getName()).orElse(null);
        if (tk == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản.");
            return "views/gdienHocVien/tai-khoan";
        }

        if (taiKhoanRepository.findByEmail(dto.getEmail()).isPresent()) {
            result.rejectValue("email", null, "Email đã tồn tại.");
        }

        if (result.hasErrors()) {
            addCommonModel(model, tk, new TaiKhoanUpdateDto(), dto, new TaiKhoanPasswordDto());
            return "views/gdienHocVien/tai-khoan";
        }

        session.setAttribute("pendingEmailUpdate", dto.getEmail());
        session.setAttribute("currentEmail", tk.getEmail());
        session.setAttribute("currentUserName", tk.getName());

        tokenService.generateAndSendToken(dto.getEmail(), tk.getName(),
                "Xác minh thay đổi email", "Mã xác minh của bạn là:");

        redirectAttributes.addFlashAttribute("message", "Email đã được cập nhật thành công.");
        redirectAttributes.addFlashAttribute("tab", "tab-email");
        return "redirect:/auth/verify?type=update-email";
    }

    @PostMapping("/tai-khoan/doi-mat-khau")
    public String doiMatKhau(
            @Valid @ModelAttribute("passwordDto") TaiKhoanPasswordDto dto,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        TaiKhoan tk = taiKhoanRepository.findByEmail(authentication.getName()).orElse(null);
        if (tk == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản.");
            return "views/gdienHocVien/tai-khoan";
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), tk.getPassword())) {
            result.rejectValue("oldPassword", null, "Mật khẩu hiện tại không đúng.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Mật khẩu xác nhận không khớp.");
        }

        if (result.hasErrors()) {
            addCommonModel(model, tk, new TaiKhoanUpdateDto(), new TaiKhoanEmailDto(), dto);
            return "views/gdienHocVien/tai-khoan";
        }

        tk.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        taiKhoanRepository.save(tk);

        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công.");
        redirectAttributes.addFlashAttribute("tab", "tab-matkhau");
        return "redirect:/tai-khoan";
    }
}
