package com.duantn.controllers.controllerHocVien;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duantn.entities.*;
import com.duantn.enums.LoaiBaiGiang;
import com.duantn.services.*;

@Controller
public class XemKhoaHocController {

    @Autowired
    private KhoaHocService khoaHocService;
    @Autowired
    private ChuongService chuongService;
    @Autowired
    private BaiGiangService baiGiangService;
    @Autowired
    private TienDoHocService tienDoHocService;
    @Autowired
    private DangHocService dangHocService;
    @Autowired
    private BinhLuanService binhLuanService;
    @Autowired
    private AuthService authService;

    @RequestMapping("/khoa-hoc")
    public String xemkhoahoc(Model model) {
        return "views/gdienHocVien/xem-khoa-hoc";
    }

    @RequestMapping("/khoa-hoc/slug/{slug}")
    public String hocbaicungto(@PathVariable("slug") String slug, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(auth);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        KhoaHoc khoaHoc = khoaHocService.getKhoaHocBySlug(slug);
        if (khoaHoc == null)
            return "redirect:/khoa-hoc?error=notfound";

        themDuLieuChungVaoModel(taiKhoan, khoaHoc, model);
        List<Chuong> chuongs = chuongService.findByKhoaHocId(khoaHoc.getKhoahocId());
        BaiGiang baiGiangDauTien = layBaiGiangDauTien(chuongs);

        if (baiGiangDauTien != null) {
            addBaiGiangToModel(baiGiangDauTien, model);
            addBinhLuanToModel(baiGiangDauTien.getBaiGiangId(), model);
            model.addAttribute("chuongDangMoId", baiGiangDauTien.getChuong().getChuongId());

            if (baiGiangDauTien.getLoaiBaiGiang() == LoaiBaiGiang.TRACNGHIEM) {
                model.addAttribute("thuTuBaiTracNghiem",
                        tinhThuTuBaiTracNghiem(baiGiangDauTien.getBaiGiangId(), chuongs));
                model.addAttribute("tongSoCauHoi", baiGiangDauTien.getTracNghiem().getCauHoiList().size());
            }
        }

        return "views/gdienHocVien/xem-khoa-hoc";
    }

    @RequestMapping("/khoa-hoc/bai-giang/{id}")
    public String xemBaiGiang(@PathVariable("id") Integer baiGiangId, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan taiKhoan = authService.getTaiKhoanFromAuth(auth);
        if (taiKhoan == null) {
            return "redirect:/auth/dangnhap";
        }

        BaiGiang baiGiang = baiGiangService.findBaiGiangById(baiGiangId);
        if (baiGiang == null)
            return "redirect:/khoa-hoc?error=notfound";

        Chuong chuong = baiGiang.getChuong();
        if (chuong == null || chuong.getKhoahoc() == null)
            return "redirect:/khoa-hoc?error=nodata";

        KhoaHoc khoaHoc = chuong.getKhoahoc();
        themDuLieuChungVaoModel(taiKhoan, khoaHoc, model);

        model.addAttribute("baiGiang", baiGiang);
        model.addAttribute("baiGiangDangHocId", baiGiangId);
        model.addAttribute("chuongDangMoId", chuong.getChuongId());

        switch (baiGiang.getLoaiBaiGiang()) {
            case VIDEO -> {
                model.addAttribute("video", baiGiang.getVideoBaiGiang());
                model.addAttribute("videoBaiGiang", baiGiang.getVideoBaiGiang());
            }
            case TAILIEU -> model.addAttribute("baiViet", baiGiang.getBaiViet());
            case TRACNGHIEM -> {
                model.addAttribute("baiTracNghiem", baiGiang.getTracNghiem());
                model.addAttribute("thuTuBaiTracNghiem",
                        tinhThuTuBaiTracNghiem(baiGiangId, chuongService.findByKhoaHocId(khoaHoc.getKhoahocId())));
                model.addAttribute("tongSoCauHoi", baiGiang.getTracNghiem().getCauHoiList().size());
            }
        }

        addBinhLuanToModel(baiGiangId, model);
        return "views/gdienHocVien/xem-khoa-hoc";
    }

    private void themDuLieuChungVaoModel(TaiKhoan taiKhoan, KhoaHoc khoaHoc, Model model) {
        model.addAttribute("taiKhoanId", taiKhoan.getTaikhoanId());
        model.addAttribute("khoaHoc", khoaHoc);
        List<Chuong> chuongs = chuongService.findByKhoaHocId(khoaHoc.getKhoahocId());
        model.addAttribute("chuongs", chuongs);
        DangHoc dangHoc = dangHocService.findByTaiKhoanIdAndKhoaHocId(taiKhoan.getTaikhoanId(), khoaHoc.getKhoahocId());

        if (dangHoc != null)
            themTienDoVaoModel(dangHoc, chuongs, model);
        else {
            model.addAttribute("phanTramHoanThanh", 0);
            model.addAttribute("baiGiangDaHoanThanhMap", Map.of());
        }
    }

    private void themTienDoVaoModel(DangHoc dangHoc, List<Chuong> chuongs, Model model) {
        List<TienDoHoc> dsTienDo = tienDoHocService.findByDangHocId(dangHoc.getDanghocId());
        if (dsTienDo.isEmpty()) {
            tienDoHocService.taoTienDoChoDangHoc(dangHoc);
            dsTienDo = tienDoHocService.findByDangHocId(dangHoc.getDanghocId());
        }

        Map<Integer, Boolean> baiGiangDaHoanThanhMap = dsTienDo.stream()
                .collect(Collectors.toMap(td -> td.getBaiGiang().getBaiGiangId(), TienDoHoc::isTrangthai));

        int tongSoBai = chuongs.stream().flatMap(c -> c.getBaiGiangs().stream()).toList().size();
        int soBaiHoanThanh = (int) baiGiangDaHoanThanhMap.values().stream().filter(v -> v).count();
        int phanTram = tongSoBai > 0 ? (int) ((double) soBaiHoanThanh / tongSoBai * 100) : 0;

        model.addAttribute("phanTramHoanThanh", phanTram);
        model.addAttribute("baiGiangDaHoanThanhMap", baiGiangDaHoanThanhMap);
    }

    private BaiGiang layBaiGiangDauTien(List<Chuong> chuongs) {
        for (Chuong chuong : chuongs) {
            if (chuong.getBaiGiangs() != null && !chuong.getBaiGiangs().isEmpty()) {
                return chuong.getBaiGiangs().get(0);
            }
        }
        return null;
    }

    private int tinhThuTuBaiTracNghiem(Integer baiGiangId, List<Chuong> chuongs) {
        int stt = 0;
        for (Chuong c : chuongs) {
            if (c.getBaiGiangs() == null)
                continue;
            for (BaiGiang bg : c.getBaiGiangs()) {
                if (bg.getLoaiBaiGiang() == LoaiBaiGiang.TRACNGHIEM) {
                    stt++;
                    if (bg.getBaiGiangId().equals(baiGiangId))
                        return stt;
                }
            }
        }
        return 0;
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
}
