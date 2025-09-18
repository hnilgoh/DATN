package com.duantn.controllers.controllerGiangVien;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import com.duantn.entities.BaiGiang;
import com.duantn.entities.BinhLuan;
import com.duantn.entities.Chuong;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.LoaiBaiGiang;
import com.duantn.services.AuthService;
import com.duantn.services.BaiGiangService;
import com.duantn.services.BinhLuanService;
import com.duantn.services.ChuongService;
import com.duantn.services.KhoaHocService;

@Controller
@RequestMapping("/noi-dung-khoa-hoc-chi-tiet")
public class NoiDungKhoaHocController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private ChuongService chuongService;

    @Autowired
    private BinhLuanService binhLuanService;

    @Autowired
    BaiGiangService baiGiangService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{id}")
    public String chiTietKhoaHoc(
            @PathVariable("id") Integer id, Model model,
            @RequestParam(value = "back", required = false) String back) {

        KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(id);
        if (khoaHoc == null) {
            return "redirect:/duyetChiTiet/danh-sach?error=notfound";
        }

        List<Chuong> chuongs = chuongService != null ? chuongService.findByKhoaHocId(id) : null;

        boolean coNoiDung = false;
        if (chuongs != null) {
            for (Chuong c : chuongs) {
                if (c.getBaiGiangs() != null && !c.getBaiGiangs().isEmpty()) {
                    coNoiDung = true;
                    break;
                }
            }
        }

        model.addAttribute("khoaHoc", khoaHoc);
        model.addAttribute("chuongs", chuongs);
        model.addAttribute("coNoiDung", coNoiDung);
        model.addAttribute("back", back);
        return "views/KhoaHoc/noi-dung-khoa-hoc-chi-tiet";
    }

    @RequestMapping("/khoahoc/slug/{slug}")
    public String noidung(@PathVariable("slug") String slug, Model model,
            @RequestParam(value = "back", required = false) String back) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
            return "views/KhoaHoc/noi-dung-khoa-hoc-chi-tiet";
        }

        if (chuongs != null && !chuongs.isEmpty()) {
            for (Chuong chuong : chuongs) {
                if (chuong.getBaiGiangs() != null && !chuong.getBaiGiangs().isEmpty()) {
                    baiGiangDauTien = chuong.getBaiGiangs().get(0);
                    chuongDangMoId = chuong.getChuongId();
                    break;
                }
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
                model.addAttribute("tongSoCauHoi", baiGiangDauTien.getTracNghiem().getCauHoiList().size());
            }
        }

        model.addAttribute("back", back);
        return "views/KhoaHoc/noi-dung-khoa-hoc-chi-tiet";
    }

    @RequestMapping("/khoa-hoc/noi-dung-bai-giang/{id}")
    public String chitietnoidung(@PathVariable("id") Integer baiGiangId, Model model,
            @RequestParam(value = "back", required = false) String back) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

        model.addAttribute("back", back);

        return "views/KhoaHoc/noi-dung-khoa-hoc-chi-tiet";
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(authentication);
        String loggedInEmail = taiKhoan != null ? taiKhoan.getEmail() : null;

        model.addAttribute("rootComments", rootComments);
        model.addAttribute("childrenMap", childrenMap);
        model.addAttribute("loggedInEmail", loggedInEmail);
    }
}
