package com.duantn.controllers.controllerGiangVien;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.duantn.entities.KhoaHoc;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.services.CloudinaryService;
import com.duantn.services.DanhMucService;
import com.duantn.services.KhoaHocService;
import com.duantn.services.TaiKhoanService;
import com.duantn.validators.SlugUtil;

@Controller
public class ThemKhoaHocController {

    @Autowired
    DanhMucService danhMucService;

    @Autowired
    TaiKhoanService taiKhoanService;

    @Autowired
    KhoaHocService khoaHocService;

    @Autowired
    CloudinaryService cloudinaryService;

    // Hiển thị form bước 1
    @GetMapping("/giangvien/them-moi-khoa-hoc")
    public String showCourseForm(@RequestParam(value = "khoahocId", required = false) Integer khoahocId,
            Model model) {
        KhoaHoc khoaHoc;
        if (khoahocId != null) {
            khoaHoc = khoaHocService.getKhoaHocById(khoahocId);
        } else {
            khoaHoc = new KhoaHoc();
        }

        model.addAttribute("course", khoaHoc);
        model.addAttribute("danhmuc", danhMucService.layTatCa());
        return "views/gdienGiangVien/them-khoa-hoc";
    }

    // Lưu thông tin cơ bản và chuyển qua bước 2
    @PostMapping("/giangvien/them-moi-khoa-hoc/save-info")
    public String saveBasicInfo(@ModelAttribute("course") KhoaHoc formCourse,
            BindingResult result, Model model,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("danhMuc.danhmucId") Integer danhMucId,
            Principal principal) throws IOException {

        boolean hasError = false;

        if (formCourse.getTenKhoaHoc() == null || formCourse.getTenKhoaHoc().trim().isEmpty()) {
            result.rejectValue("tenKhoaHoc", "error.course", "Tên khóa học không được để trống");
            hasError = true;
        } else if (formCourse.getTenKhoaHoc().trim().length() < 3) {
            result.rejectValue("tenKhoaHoc", "error.course", "Tên khóa học phải có ít nhất 3 ký tự");
            hasError = true;
        }

        if (formCourse.getMoTa() == null || formCourse.getMoTa().trim().isEmpty()) {
            result.rejectValue("moTa", "error.course", "Mô tả không được để trống");
            hasError = true;
        } else if (formCourse.getMoTa().trim().length() < 20) {
            result.rejectValue("moTa", "error.course", "Mô tả phải có ít nhất 20 ký tự");
            hasError = true;
        }

        boolean isCreating = formCourse.getKhoahocId() == null;
        if (isCreating && (file == null || file.isEmpty())) {
            model.addAttribute("imageError", "Vui lòng chọn ảnh bìa cho khóa học");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("danhmuc", danhMucService.layTatCa());
            return "views/gdienGiangVien/them-khoa-hoc";
        }

        KhoaHoc khoahoc;

        // ✅ Nếu có ID thì load từ DB để cập nhật
        if (formCourse.getKhoahocId() != null) {
            khoahoc = khoaHocService.layTheoId(formCourse.getKhoahocId());
            if (khoahoc == null) {
                // fallback nếu không tồn tại
                khoahoc = new KhoaHoc();
            }
        } else {
            khoahoc = new KhoaHoc();
        }

        // Cập nhật lại thông tin từ form
        khoahoc.setTenKhoaHoc(formCourse.getTenKhoaHoc());
        khoahoc.setMoTa(formCourse.getMoTa());
        khoahoc.setDanhMuc(danhMucService.layTheoId(danhMucId));
        khoahoc.setUrlGioiThieu(formCourse.getUrlGioiThieu());
        khoahoc.setGiangVien(taiKhoanService.findByUsername(principal.getName()).getGiangVien());
        khoahoc.setTrangThai(TrangThaiKhoaHoc.DRAFT);

        if (file != null && !file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(file);
            khoahoc.setAnhBia(imageUrl);
            khoahoc.setAnhBiaPublicId(cloudinaryService.extractPublicIdFromUrl(imageUrl));
        }

        // Save
        khoaHocService.save(khoahoc);

        String slug = SlugUtil.toSlug(khoahoc.getTenKhoaHoc()) + khoahoc.getKhoahocId();
        khoahoc.setSlug(slug);

        khoaHocService.save(khoahoc);

        return "redirect:/giangvien/them-moi-khoa-hoc/gia?khoahocId=" + khoahoc.getKhoahocId();
    }

    // Hiển thị form bước 2
    @GetMapping("/giangvien/them-moi-khoa-hoc/gia")
    public String showGiaForm(@RequestParam("khoahocId") Integer khoahocId, Model model) {
        KhoaHoc khoahoc = khoaHocService.getKhoaHocById(khoahocId);

        if (khoahoc == null) {
            return "redirect:/giangvien/khoa-hoc";
        }

        model.addAttribute("course", khoahoc);
        return "views/gdienGiangVien/them-gia-khoa-hoc";
    }

    // Lưu giá và hoàn tất
    @PostMapping("/giangvien/them-moi-khoa-hoc/save-price")
    public String savePrice(@ModelAttribute("course") KhoaHoc khoahocUpdate, BindingResult result, Model model) {
        boolean hasError = false;

        // 1. Kiểm tra giá gốc
        if (khoahocUpdate.getGiagoc() == null || khoahocUpdate.getGiagoc().compareTo(new BigDecimal("1000")) < 0) {
            result.rejectValue("giagoc", "error.course", "Giá phải từ 1.000 VNĐ trở lên");
            hasError = true;
        }

        // 2. Kiểm tra phần trăm giảm
        Integer ptg = khoahocUpdate.getPhanTramGiam();
        if (ptg != null) {
            if (ptg < 0 || ptg > 100) {
                result.rejectValue("phanTramGiam", "error.course", "Phần trăm giảm phải từ 0 đến 100");
                hasError = true;
            }

            // 3. Nếu có giảm giá > 0 → bắt buộc phải có ngày bắt đầu và kết thúc
            if (ptg > 0) {
                if (khoahocUpdate.getNgaybatdau() == null) {
                    result.rejectValue("ngaybatdau", "error.course", "Vui lòng nhập ngày bắt đầu khuyến mãi");
                    hasError = true;
                } else {
                    LocalDate today = LocalDate.now();
                    LocalDate ngayBatDau = khoahocUpdate.getNgaybatdau().toLocalDate();

                    if (ngayBatDau.isBefore(today)) {
                        result.rejectValue("ngaybatdau", "error.course",
                                "Khuyến mãi phải bắt đầu từ thời gian hiện tại");
                        hasError = true;
                    }
                }

                if (khoahocUpdate.getNgayketthuc() == null) {
                    result.rejectValue("ngayketthuc", "error.course", "Vui lòng nhập ngày kết thúc khuyến mãi");
                    hasError = true;
                }

                // Nếu cả 2 ngày đã có, kiểm tra hợp lệ
                if (khoahocUpdate.getNgaybatdau() != null && khoahocUpdate.getNgayketthuc() != null) {
                    if (khoahocUpdate.getNgayketthuc().isBefore(khoahocUpdate.getNgaybatdau())) {
                        result.rejectValue("ngayketthuc", "error.course", "Ngày kết thúc phải sau ngày bắt đầu");
                        hasError = true;
                    }
                }
            }
        }

        // Nếu có lỗi → quay lại form
        if (hasError) {
            model.addAttribute("course", khoahocUpdate);
            return "views/gdienGiangVien/them-gia-khoa-hoc";
        }

        // Không có lỗi → lưu dữ liệu
        KhoaHoc khoahoc = khoaHocService.getKhoaHocById(khoahocUpdate.getKhoahocId());
        if (khoahoc == null) {
            return "redirect:/giangvien/khoa-hoc";
        }

        khoahoc.setGiagoc(khoahocUpdate.getGiagoc());
        khoahoc.setPhanTramGiam(khoahocUpdate.getPhanTramGiam());
        khoahoc.setNgaybatdau(khoahocUpdate.getNgaybatdau());
        khoahoc.setNgayketthuc(khoahocUpdate.getNgayketthuc());

        // Tính giá khuyến mãi nếu có giảm
        if (khoahoc.getGiagoc() != null && khoahoc.getPhanTramGiam() != null && khoahoc.getPhanTramGiam() > 0) {
            BigDecimal giam = khoahoc.getGiagoc()
                    .multiply(BigDecimal.valueOf(khoahoc.getPhanTramGiam()))
                    .divide(BigDecimal.valueOf(100));
            khoahoc.setGiaKhuyenMai(khoahoc.getGiagoc().subtract(giam));
        } else {
            khoahoc.setGiaKhuyenMai(null);
            khoahoc.setNgaybatdau(null);
            khoahoc.setNgayketthuc(null);
        }

        khoahoc.setShare("http://localhost:8080/khoa-hoc/" + khoahoc.getKhoahocId());
        khoaHocService.save(khoahoc);

        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahoc.getKhoahocId();
    }

}
