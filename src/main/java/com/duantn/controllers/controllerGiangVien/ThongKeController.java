package com.duantn.controllers.controllerGiangVien;

import com.duantn.dtos.DoanhThuKhoaHocGiangVienDto;
import com.duantn.dtos.HocVienDto;
import com.duantn.dtos.HocVienTheoKhoaHocDto;
import com.duantn.dtos.KhoaHocDiemDto;
import com.duantn.entities.GiangVien;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.AuthService;
import com.duantn.services.DangHocService;
import com.duantn.services.GiangVienService;
import com.duantn.services.TaiKhoanService;
import com.duantn.services.ThongKeService;
import com.duantn.services.ViGiangVienService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/giangvien")
public class ThongKeController {

    @Autowired
    private GiangVienService giangVienService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private DangHocService dangHocService;

    @Autowired
    private ViGiangVienService viGiangVienService;

    @Autowired
    private ThongKeService thongKeService;

    @RequestMapping("/danh-sach-thong-ke")
    public String showThongKe(Authentication authentication, Model model) {
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien giangVien = giangVienService.findByTaikhoan(taiKhoan);
        if (giangVien == null) {
            return "redirect:/auth/dangnhap";
        }

        Integer giangVienId = giangVien.getGiangvienId();
        List<DoanhThuKhoaHocGiangVienDto> doanhThuKhoaHoc = giangVienService.thongKeDoanhThuTheoGiangVien(giangVienId);

        double tongTienNhan = giangVienService.layTongTienNhan(giangVienId);
        long tongHocVien = giangVienService.demHocVienTheoGiangVien(giangVienId);

        // >>> thêm dữ liệu cho chart
        Map<Integer, Double> doanhThuThang = thongKeService.layDoanhThuTheoThang(taiKhoan);
        model.addAttribute("labels", doanhThuThang.keySet()); // [1,2,3...]
        model.addAttribute("data", doanhThuThang.values());

        model.addAttribute("doanhThuKhoaHoc", doanhThuKhoaHoc);
        model.addAttribute("tongTienNhan", tongTienNhan);
        model.addAttribute("tongHocVien", tongHocVien);
        model.addAttribute("soKhoaHoc", doanhThuKhoaHoc.size());
        model.addAttribute("diemTrungBinh", giangVienService.tinhDiemDanhGiaTrungBinh(giangVienId));
        model.addAttribute("tongThuThang", viGiangVienService.getTongThuTrongThang(taiKhoan));

        return "views/gdienGiangVien/thong-ke";
    }

    @GetMapping("/thong-ke-hoc-vien")
    public String thongKeHocVienTheoKhoaHoc(
            @RequestParam(value = "khoaHocId", required = false) Integer khoaHocId,
            Authentication authentication,
            Model model) {

        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien giangVien = giangVienService.findByTaikhoan(taiKhoan);
        if (giangVien == null) {
            return "redirect:/giangvien/trang-giang-vien";
        }

        // Danh sách khóa học có học viên (dropdown)
        List<HocVienTheoKhoaHocDto> dsKhoaHocCoHocVien = giangVienService
                .thongKeHocVienTheoKhoaHoc(giangVien.getGiangvienId());

        model.addAttribute("dsKhoaHocCoHocVien", dsKhoaHocCoHocVien);

        // Nếu chọn khóa học thì lấy học viên
        if (khoaHocId != null) {
            List<HocVienDto> dsHocVien = giangVienService.findHocVienTheoKhoaHoc(khoaHocId);

            String tenKhoaHocDaChon = dsKhoaHocCoHocVien.stream()
                    .filter(kh -> kh.getKhoaHocId().equals(khoaHocId))
                    .map(HocVienTheoKhoaHocDto::getTenKhoaHoc)
                    .findFirst()
                    .orElse("Khóa học");

            model.addAttribute("dsHocVien", dsHocVien);
            model.addAttribute("selectedKhoaHocId", khoaHocId);
            model.addAttribute("tenKhoaHocDaChon", tenKhoaHocDaChon);
        }

        return "views/gdienGiangVien/thong-ke-chi-tiet-hvien";
    }

    @GetMapping("/chi-tiet-hoc-vien/{id}")
    public String chiTietHocVien(@PathVariable("id") Integer hocVienId, Model model) {
        TaiKhoan hv = taiKhoanService.findById(hocVienId).get();

        List<KhoaHoc> dsKhoaHoc = dangHocService.findKhoaHocByHocVienId(hocVienId);

        model.addAttribute("hocVien", hv);
        model.addAttribute("dsKhoaHoc", dsKhoaHoc);

        return "views/gdienGiangVien/chi-tiet-khoa-hoc-hv";
    }

    @GetMapping("/khoa-hoc-dang-day")
    public String danhSachKhoaHocDangDay(Authentication authentication, Model model) {
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien gv = giangVienService.findByTaikhoan(taiKhoan);
        if (gv == null) {
            return "redirect:/giangvien/trang-giang-vien";
        }

        List<KhoaHoc> khoaHocDangDay = giangVienService.timTatCaKhoaHocDangDay(gv.getGiangvienId());
        model.addAttribute("danhSachKhoaHoc", khoaHocDangDay);

        return "views/gdienGiangVien/thong-ke-khoa-hoc-dang-day";
    }

    @GetMapping("/danh-gia-trung-binh")
    public String danhGiaTrungBinhTheoKhoaHoc(Authentication authentication, Model model) {
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        GiangVien gv = giangVienService.findByTaikhoan(taiKhoan);
        if (gv == null) {
            return "redirect:/giangvien/trang-giang-vien";
        }

        List<KhoaHocDiemDto> danhGiaList = giangVienService.layDiemTrungBinhCacKhoaHocXuatBan(gv.getGiangvienId());
        model.addAttribute("danhGiaList", danhGiaList);

        return "views/gdienGiangVien/thong-ke-danh-gia-trung-binh";
    }
}
