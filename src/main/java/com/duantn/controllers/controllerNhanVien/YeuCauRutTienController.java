package com.duantn.controllers.controllerNhanVien;

import com.duantn.entities.RutTienGiangVien;
import com.duantn.enums.TrangThaiRutTien;
import com.duantn.repositories.RutTienGiangVienRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping({ "/admin/yeu-cau-rut-tien", "/nhanvien/yeu-cau-rut-tien" })
@PreAuthorize("hasAnyRole('ADMIN', 'NHANVIEN')")
public class YeuCauRutTienController {
    private final RutTienGiangVienRepository rutTienRepo;

    public YeuCauRutTienController(RutTienGiangVienRepository rutTienRepo) {
        this.rutTienRepo = rutTienRepo;
    }

    @GetMapping
    public String hienThiDanhSach(Model model) {
        List<RutTienGiangVien> danhSachThanhCong = rutTienRepo.findByTrangthai(TrangThaiRutTien.THANH_CONG);
        List<RutTienGiangVien> danhSachTuChoi = rutTienRepo.findByTrangthai(TrangThaiRutTien.TU_CHOI);
        List<RutTienGiangVien> danhSachDangCho = rutTienRepo.findByTrangthai(TrangThaiRutTien.DANG_CHO_XU_LY);

        model.addAttribute("danhSachThanhCong", danhSachThanhCong);
        model.addAttribute("danhSachTuChoi", danhSachTuChoi);
        model.addAttribute("danhSachDangCho", danhSachDangCho);
        return "views/gdienQuanLy/yeu-cau-rut-tien";
    }

    @GetMapping("/{id}")
    public String xemChiTiet(@PathVariable Integer id, Model model) {
        RutTienGiangVien yeuCau = rutTienRepo.findById(id).orElse(null);
        if (yeuCau == null) {
            return "redirect:./"; // về danh sách
        }
        model.addAttribute("yeuCau", yeuCau);
        return "views/gdienQuanLy/chi-tiet-yeu-cau-rut-tien";
    }

    @PostMapping("/{id}/duyet")
    public String duyetYeuCauChiTiet(RedirectAttributes redirectAttributes,
            @PathVariable Integer id, @RequestHeader("referer") String referer) {
        RutTienGiangVien yeuCau = rutTienRepo.findById(id).orElse(null);
        if (yeuCau != null && yeuCau.getTrangthai() == TrangThaiRutTien.DANG_CHO_XU_LY) {
            yeuCau.setTrangthai(TrangThaiRutTien.THANH_CONG);
            rutTienRepo.save(yeuCau);
            redirectAttributes.addFlashAttribute("msg", "success");
        }
        return "redirect:" + referer;
    }

    @PostMapping("/{id}/tuchoi")
    public String tuChoiYeuCauChiTiet(RedirectAttributes redirectAttributes,
            @PathVariable Integer id, @RequestHeader("referer") String referer) {
        RutTienGiangVien yeuCau = rutTienRepo.findById(id).orElse(null);
        if (yeuCau != null && yeuCau.getTrangthai() == TrangThaiRutTien.DANG_CHO_XU_LY) {
            yeuCau.setTrangthai(TrangThaiRutTien.TU_CHOI);
            rutTienRepo.save(yeuCau);
            redirectAttributes.addFlashAttribute("msg", "rejected");
        }
        return "redirect:" + referer;
    }
}