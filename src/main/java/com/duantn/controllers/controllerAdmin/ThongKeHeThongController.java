package com.duantn.controllers.controllerAdmin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BinhLuan;
import com.duantn.entities.Chuong;
import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.LoaiBaiGiang;
import com.duantn.services.*;

@Controller
@RequestMapping("/{prefix:(?:admin|nhanvien)}")
public class ThongKeHeThongController {

        @Autowired
        private ThongKeService thongKeService;

        @Autowired
        private DangHocService dangHocService;

        @Autowired
        private KhoaHocService khoaHocService;

        @Autowired
        private ChuongService chuongService;

        @Autowired
        private DanhGiaService danhGiaService;

        @Autowired
        private BinhLuanService binhLuanService;

        @Autowired
        BaiGiangService baiGiangService;

        @Autowired
        private AuthService authService;

        // Danh sách khóa học hệ thống
        @GetMapping("/khoa-hoc-he-thong")
        public String loadDashboard(@PathVariable String prefix, Model model) {

                // 🟢 Luôn truyền prefix sang view
                model.addAttribute("prefix", prefix);

                List<String> labels = thongKeService.getTopKhoaHocLabels();
                List<Long> soLuong = thongKeService.getTopKhoaHocSoLuong();
                List<KhoaHoc> khoaHocDaXuatBan = thongKeService.getAllKhoaHocDaXuatBan();

                // Top 3 giảng viên doanh thu cao nhất
                List<Object[]> topDoanhThu = thongKeService.getTop3GiangVienDoanhThu();
                List<String> doanhThuLabels = topDoanhThu.stream()
                                .map(row -> (String) row[0])
                                .toList();
                List<BigDecimal> doanhThuData = topDoanhThu.stream()
                                .map(row -> (BigDecimal) row[1])
                                .toList();

                // Top 5 giảng viên học viên nhiều nhất
                List<Object[]> topHocVien = thongKeService.getTop5GiangVienHocVien();
                List<String> hocVienLabels = topHocVien.stream()
                                .map(row -> (String) row[0])
                                .toList();
                List<Long> hocVienData = topHocVien.stream()
                                .map(row -> (Long) row[1])
                                .toList();

                model.addAttribute("doanhThuLabels", doanhThuLabels);
                model.addAttribute("doanhThuData", doanhThuData);
                model.addAttribute("hocVienLabels", hocVienLabels);
                model.addAttribute("hocVienData", hocVienData);
                model.addAttribute("topKhoaHocLabels", labels);
                model.addAttribute("topKhoaHocSoLuong", soLuong);
                model.addAttribute("khoaHocDaXuatBan", khoaHocDaXuatBan);

                return "views/gdienQuanLy/khoa-hoc-cua-he-thong";
        }

        // Chi tiết khóa học
        @GetMapping("/tong-quan-khoa-hoc/{id}")
        public String chiTietKhoaHoc(@PathVariable String prefix,
                        @PathVariable("id") Integer id,
                        Authentication authentication,
                        Model model) {
                model.addAttribute("prefix", prefix);

                KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(id);
                if (khoaHoc == null) {
                        return "redirect:/" + prefix + "/khoa-hoc-he-thong?error=notfound";
                }

                List<Chuong> chuongs = chuongService.findByKhoaHocId(id);
                long soLuongDangKy = dangHocService.demSoLuongDangKy(id);
                long soLuongDanhGia = danhGiaService.demSoLuongDanhGia(id);
                Double diemTrungBinh = danhGiaService.diemTrungBinh(id);

                TaiKhoan user = authService.getTaiKhoanFromAuth(authentication); // 🟢 Dùng AuthService
                DanhGia danhGia = new DanhGia();

                if (user != null) {
                        danhGia = danhGiaService.findByTaikhoanAndKhoahoc(user, khoaHoc).orElse(new DanhGia());
                        boolean isEnrolled = dangHocService.isEnrolled(user.getTaikhoanId(), id);
                        model.addAttribute("isEnrolled", isEnrolled);
                }

                model.addAttribute("khoaHoc", khoaHoc);
                model.addAttribute("chuongs", chuongs);
                model.addAttribute("soLuongDangKy", soLuongDangKy);
                model.addAttribute("soLuongDanhGia", soLuongDanhGia);
                model.addAttribute("diemTrungBinh", diemTrungBinh);
                model.addAttribute("danhGiaList", danhGiaService.findByKhoaHocId(id));
                model.addAttribute("danhGiaMoi", danhGia);

                return "views/gdienQuanLy/tong-quan-khoa-hoc";
        }

        @RequestMapping("/tong-quan-khoa-hoc/slug/{slug}")
        public String noidung(@PathVariable("slug") String slug,
                        @PathVariable String prefix,
                        Authentication authentication,
                        Model model) {
                model.addAttribute("prefix", prefix);

                TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
                if (taiKhoan == null) {
                        return "redirect:/auth/dangnhap";
                }
                model.addAttribute("taiKhoanId", taiKhoan.getTaikhoanId());

                KhoaHoc khoaHoc = khoaHocService.getKhoaHocBySlug(slug);
                if (khoaHoc == null) {
                        return "redirect:/khoaHoc?error=notfound";
                }

                Integer id = khoaHoc.getKhoahocId();
                List<Chuong> chuongs = chuongService.findByKhoaHocId(id);

                BaiGiang baiGiangDauTien = null;
                Integer chuongDangMoId = null;

                if (chuongs == null || chuongs.isEmpty()) {
                        model.addAttribute("message", "Khóa học hiện chưa có chương và bài giảng nào.");
                        model.addAttribute("khoaHoc", khoaHoc);
                        return "views/gdienQuanLy/khoa-hoc-he-thong-chi-tiet";
                }

                for (Chuong chuong : chuongs) {
                        if (chuong.getBaiGiangs() != null && !chuong.getBaiGiangs().isEmpty()) {
                                baiGiangDauTien = chuong.getBaiGiangs().get(0);
                                chuongDangMoId = chuong.getChuongId();
                                break;
                        }
                }

                model.addAttribute("khoaHoc", khoaHoc);
                model.addAttribute("chuongs", chuongs);
                model.addAttribute("chuongDangMoId", chuongDangMoId);

                if (baiGiangDauTien != null) {
                        addBaiGiangToModel(baiGiangDauTien, model);
                        addBinhLuanToModel(baiGiangDauTien.getBaiGiangId(), model);

                        if (baiGiangDauTien.getLoaiBaiGiang() == LoaiBaiGiang.TRACNGHIEM) {
                                int stt = tinhThuTuBaiTracNghiem(baiGiangDauTien.getBaiGiangId(), chuongs);
                                model.addAttribute("thuTuBaiTracNghiem", stt);
                                model.addAttribute("tongSoCauHoi",
                                                baiGiangDauTien.getTracNghiem().getCauHoiList().size());
                        }
                }

                return "views/gdienQuanLy/khoa-hoc-he-thong-chi-tiet";
        }

        @RequestMapping("/tong-quan-khoa-hoc-chi-tiet/noi-dung-bai-giang/{id}")
        public String chitietnoidung(@PathVariable("id") Integer baiGiangId,
                        @PathVariable String prefix,
                        Authentication authentication,
                        Model model) {
                model.addAttribute("prefix", prefix);

                TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
                if (taiKhoan == null) {
                        return "redirect:/auth/dangnhap";
                }
                model.addAttribute("taiKhoanId", taiKhoan.getTaikhoanId());

                BaiGiang baiGiang = baiGiangService.findBaiGiangById(baiGiangId);
                if (baiGiang == null) {
                        return "redirect:/khoa-hoc?error=notfound";
                }

                Chuong chuong = baiGiang.getChuong();
                if (chuong == null || chuong.getKhoahoc() == null) {
                        return "redirect:/khoa-hoc?error=nodata";
                }

                KhoaHoc khoaHoc = chuong.getKhoahoc();
                List<Chuong> chuongs = chuongService.findByKhoaHocId(khoaHoc.getKhoahocId());

                model.addAttribute("khoaHoc", khoaHoc);
                model.addAttribute("chuongs", chuongs);
                model.addAttribute("baiGiang", baiGiang);
                model.addAttribute("baiGiangDangHocId", baiGiangId);
                model.addAttribute("chuongDangMoId", baiGiang.getChuong().getChuongId());

                addBaiGiangToModel(baiGiang, model);
                addBinhLuanToModel(baiGiangId, model);

                if (baiGiang.getLoaiBaiGiang() == LoaiBaiGiang.TRACNGHIEM) {
                        int stt = tinhThuTuBaiTracNghiem(baiGiang.getBaiGiangId(), chuongs);
                        model.addAttribute("thuTuBaiTracNghiem", stt);
                        model.addAttribute("tongSoCauHoi", baiGiang.getTracNghiem().getCauHoiList().size());
                }

                return "views/gdienQuanLy/khoa-hoc-he-thong-chi-tiet";
        }

        private int tinhThuTuBaiTracNghiem(Integer baiGiangId, List<Chuong> chuongs) {
                int stt = 0;
                for (Chuong c : chuongs) {
                        if (c.getBaiGiangs() == null)
                                continue;
                        for (BaiGiang bg : c.getBaiGiangs()) {
                                if (bg.getLoaiBaiGiang() == LoaiBaiGiang.TRACNGHIEM) {
                                        stt++;
                                        if (bg.getBaiGiangId().equals(baiGiangId)) {
                                                return stt;
                                        }
                                }
                        }
                }
                return 0;
        }

        private void addBaiGiangToModel(BaiGiang baiGiang, Model model) {
                model.addAttribute("baiGiang", baiGiang);
                model.addAttribute("baiGiangDangHocId", baiGiang.getBaiGiangId());
                switch (baiGiang.getLoaiBaiGiang()) {
                        case VIDEO -> {
                                model.addAttribute("video", baiGiang.getVideoBaiGiang());
                                model.addAttribute("videoBaiGiang", baiGiang.getVideoBaiGiang());
                        }
                        case TAILIEU -> model.addAttribute("baiViet", baiGiang.getBaiViet());
                        case TRACNGHIEM -> model.addAttribute("baiTracNghiem", baiGiang.getTracNghiem());
                }
        }

        private void addBinhLuanToModel(Integer baiGiangId, Model model) {
                List<BinhLuan> rootComments = binhLuanService.getCommentsByBaiGiangId(baiGiangId);
                List<BinhLuan> allComments = binhLuanService.getAllCommentsByBaiGiangId(baiGiangId);
                Map<Integer, List<BinhLuan>> childrenMap = allComments.stream()
                                .filter(c -> c.getParent() != null)
                                .collect(Collectors.groupingBy(c -> c.getParent().getBinhluanId()));
                String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
                model.addAttribute("rootComments", rootComments);
                model.addAttribute("childrenMap", childrenMap);
                model.addAttribute("loggedInEmail", loggedInEmail);
        }
}
