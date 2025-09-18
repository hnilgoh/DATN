package com.duantn.controllers.controllerNhanVien;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duantn.entities.KhoaHoc;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.services.KhoaHocService;
import com.duantn.services.ThongBaoService;

@Controller
public class DuyetKhoaHocController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private ThongBaoService thongBaoService;

    @GetMapping("/khoahoccanduyet")
    public String hienThiDanhSachKhoaHoc(Model model) {
        List<KhoaHoc> danhSach = khoaHocService.layTatCaKhoaHocCanDuyet();
        model.addAttribute("danhSach", danhSach);
        return "views/gdienQuanLy/kiemduyetKhoaHoc";
    }

    @PostMapping("/xac-nhan-phe-duyet/phe-duyet/{id}")
    public String pheDuyet(@PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        Optional<KhoaHoc> optionalKhoaHoc = khoaHocService.findById(id);

        optionalKhoaHoc.ifPresent(khoaHoc -> {
            if (khoaHoc.getTrangThai() == TrangThaiKhoaHoc.PENDING_APPROVAL) {
                khoaHoc.setTrangThai(TrangThaiKhoaHoc.PUBLISHED);
                khoaHocService.save(khoaHoc);

                String tieuDe = "✅ Khóa học \"" + khoaHoc.getTenKhoaHoc() + "\" đã được phê duyệt thành công!";
                String noiDung = "📚 Khóa học <b>\"" + khoaHoc.getTenKhoaHoc()
                        + "\"</b> của bạn đã được duyệt và <span style='color:green;'>chính thức công khai</span> trên hệ thống. "
                        + "Hãy kiểm tra lại khóa học để đảm bảo mọi thứ hiển thị đúng. Cảm ơn bạn đã đóng góp! 🙌"
                        + "👉 <a href='/giangvien/khoa-hoc-da-tao'>Xem danh sách khóa học của bạn</a>";

                Integer taiKhoanId = khoaHoc.getGiangVien().getTaikhoan().getTaikhoanId();
                thongBaoService.guiThongBao(tieuDe, noiDung, List.of(taiKhoanId));

                redirectAttributes.addFlashAttribute("message", "✅ Đã phê duyệt khóa học: " + khoaHoc.getTenKhoaHoc());
                redirectAttributes.addFlashAttribute("type", "success");

            }
        });
        return "redirect:" + getRoleBasedRedirect();
    }

    @PostMapping("/xac-nhan-phe-duyet/tu-choi/{khoahocId}")
    public String tuChoi(@PathVariable("khoahocId") Integer khoahocId,
            @RequestParam("lyDo") String lyDo,
            RedirectAttributes redirectAttributes) {

        Optional<KhoaHoc> optionalKhoaHoc = khoaHocService.findById(khoahocId);
        optionalKhoaHoc.ifPresent(khoaHoc -> {

            if (khoaHoc.getTrangThai() == TrangThaiKhoaHoc.PENDING_APPROVAL) {
                khoaHoc.setTrangThai(TrangThaiKhoaHoc.REJECTED);
                khoaHocService.save(khoaHoc);

                String tieuDe = "❌ Khóa học \"" + khoaHoc.getTenKhoaHoc() + "\" đã bị từ chối";
                String noiDung = "📌 Khóa học <b>\"" + khoaHoc.getTenKhoaHoc()
                        + "\"</b> của bạn đã bị <span style='color:red;'>từ chối</span> vì lý do sau:<br>"
                        + "<i>\"" + lyDo + "\"</i><br><br>"
                        + "👉 <a href='/giangvien/khoa-hoc-da-tao'>Hãy xem và chỉnh sửa lại!</a>";

                System.out.println("Lý do từ chối: " + lyDo);
                Integer taiKhoanId = khoaHoc.getGiangVien().getTaikhoan().getTaikhoanId();
                thongBaoService.guiThongBao(tieuDe, noiDung, List.of(taiKhoanId));

                redirectAttributes.addFlashAttribute("message", "❌ Đã từ chối khóa học: " + khoaHoc.getTenKhoaHoc());
                redirectAttributes.addFlashAttribute("type", "error");
            }
        });
        return "redirect:" + getRoleBasedRedirect();
    }

    @GetMapping("/khoa-hoc-da-kiem-duyet")
    public String danhSachKhoaHocDaKiemDuyet(Model model) {
        List<KhoaHoc> daDuyet = khoaHocService.layKhoaHocTheoTrangThai(TrangThaiKhoaHoc.PUBLISHED);
        List<KhoaHoc> daTuChoi = khoaHocService.layKhoaHocTheoTrangThai(TrangThaiKhoaHoc.REJECTED);

        model.addAttribute("daDuyet", daDuyet);
        model.addAttribute("daTuChoi", daTuChoi);

        return "views/gdienQuanLy/khoahocdakiemduyet";
    }

    private String getRoleBasedRedirect() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_NHANVIEN"))
                        ? "/khoahoccanduyet"
                        : "/access-denied";
    }

}
