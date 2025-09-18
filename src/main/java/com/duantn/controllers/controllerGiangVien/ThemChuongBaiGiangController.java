package com.duantn.controllers.controllerGiangVien;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duantn.dtos.ChuongFormWrapper;
import com.duantn.entities.BaiGiang;
import com.duantn.entities.BaiTracNghiem;
import com.duantn.entities.BaiViet;
import com.duantn.entities.Chuong;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.VideoBaiGiang;
import com.duantn.enums.LoaiBaiGiang;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.services.BaiGiangService;
import com.duantn.services.BaiTracNghiemService;
import com.duantn.services.ChuongService;
import com.duantn.services.KhoaHocService;
import com.duantn.services.BaiVietService;
import com.duantn.services.VideoBaiGiangService;

@Controller
public class ThemChuongBaiGiangController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private ChuongService chuongService;

    @Autowired
    private BaiGiangService baiGiangService;

    @Autowired
    private VideoBaiGiangService videoBaiGiangService;

    @Autowired
    private BaiVietService baivietService;

    @Autowired
    private BaiTracNghiemService baitracNghiemService;

    @GetMapping("/giangvien/them-moi-khoa-hoc/them-chuong")
    public String showChuongVaBaiGiangForm(@RequestParam("khoahocId") Integer khoahocId, Model model) {

        KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(khoahocId);
        List<Chuong> danhSachChuong = chuongService.findFullByKhoaHocId(khoahocId);

        ChuongFormWrapper wrapper = new ChuongFormWrapper();
        wrapper.setChuongs(danhSachChuong);

        model.addAttribute("khoahoc", khoaHoc);
        model.addAttribute("chuongForm", wrapper);
        return "views/gdienGiangVien/them-chuong-va-bai-giang";
    }

    // thêm chương
    @PostMapping("/giangvien/them-moi-khoa-hoc/chuong-khoa-hoc")
    public String saveChuongRieng(
            @ModelAttribute("chuongForm") ChuongFormWrapper wrapper,
            @RequestParam("saveChuongIndex") Integer saveChuongIndex,
            @RequestParam("khoahocId") Integer khoahocId,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (saveChuongIndex == null || saveChuongIndex < 0 || saveChuongIndex >= wrapper.getChuongs().size()) {
            redirectAttributes.addFlashAttribute("loi", "⚠ Không tìm thấy chương để lưu.");
            return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
        }

        Chuong chuong = wrapper.getChuongs().get(saveChuongIndex);

        List<String> danhSachLoi = kiemTraLoiDuLieu(wrapper);

        if (!danhSachLoi.isEmpty()) {
            StringBuilder thongBaoLoi = new StringBuilder();
            for (String loi : danhSachLoi) {
                thongBaoLoi.append(loi).append("<br>");
            }

            model.addAttribute("khoahoc", khoaHocService.getKhoaHocById(khoahocId));
            model.addAttribute("chuongForm", wrapper);
            model.addAttribute("loidinhdang", thongBaoLoi.toString());
            return "views/gdienGiangVien/them-chuong-va-bai-giang";
        }

        KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(khoahocId);
        chuong.setKhoahoc(khoaHoc);

        if (chuong.getChuongId() != null) {
            Chuong chuongDaCo = chuongService.findById(chuong.getChuongId());
            if (chuongDaCo != null) {
                chuongDaCo.setTenchuong(chuong.getTenchuong());
                chuongDaCo.setMota(chuong.getMota());
                chuongDaCo.setThutuchuong(chuong.getThutuchuong());
                chuongDaCo.setKhoahoc(khoaHoc);
                chuongService.save(chuongDaCo);
            } else {
                chuongService.save(chuong);
            }
        } else {
            chuongService.save(chuong);
        }

        redirectAttributes.addFlashAttribute("thongbao", "✔ Đã lưu chương: " + chuong.getTenchuong());
        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
    }

    // thêm bài giảng
    @PostMapping("/giangvien/them-moi-khoa-hoc/chuong-bai-giang")
    public String luuBaiGiangRieng(
            @ModelAttribute ChuongFormWrapper chuongForm,
            @RequestParam(required = false) String saveBaiGiang,
            @RequestParam Integer khoahocId,
            RedirectAttributes redirectAttributes) {

        if (saveBaiGiang != null) {
            try {
                // Tách index chuong và baiGiang
                String[] parts = saveBaiGiang.split("-");
                int cIndex = Integer.parseInt(parts[0]);
                int bIndex = Integer.parseInt(parts[1]);

                Chuong chuong = chuongForm.getChuongs().get(cIndex);
                if (chuong.getChuongId() == null) {
                    redirectAttributes.addFlashAttribute("loi", "Chương phải được lưu trước khi lưu bài giảng.");
                    return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
                }

                BaiGiang baiGiang = chuong.getBaiGiangs().get(bIndex);

                // Gán dữ liệu
                baiGiang.setChuong(chuong);
                baiGiang.setTrangthai(true);

                BaiGiang entity;
                if (baiGiang.getBaiGiangId() != null) {
                    BaiGiang baiGiangCu = baiGiangService.findById(baiGiang.getBaiGiangId()).orElse(null);
                    if (daThayDoiLoaiBaiGiang(baiGiang, baiGiangCu)) {
                        redirectAttributes.addFlashAttribute("loi", "❌ Không được thay đổi loại bài giảng đã lưu.");
                        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
                    }
                    entity = baiGiangCu != null ? baiGiangCu : new BaiGiang();
                } else {
                    entity = new BaiGiang();
                }

                entity.setTenBaiGiang(baiGiang.getTenBaiGiang());
                entity.setMota(baiGiang.getMota());
                entity.setTrangthai(true);
                entity.setLoaiBaiGiang(baiGiang.getLoaiBaiGiang());
                entity.setChuong(chuongService.getReferenceById(chuong.getChuongId()));

                // Lưu bài giảng trước khi xử lý các phần phụ (video, bài viết)
                BaiGiang baiGiangLuu = baiGiangService.save(entity);

                // VIDEO
                if (baiGiang.getLoaiBaiGiang() == LoaiBaiGiang.VIDEO && baiGiang.getVideoBaiGiang() != null) {
                    VideoBaiGiang video = baiGiang.getVideoBaiGiang();
                    VideoBaiGiang daCo = videoBaiGiangService.findByBaiGiangId(baiGiangLuu.getBaiGiangId());
                    if (daCo != null) {
                        daCo.setUrl_video(video.getUrl_video());
                        daCo.setMota(video.getMota());
                        videoBaiGiangService.save(daCo);
                    } else {
                        video.setBaiGiang(baiGiangLuu);
                        videoBaiGiangService.save(video);
                    }
                }

                // BÀI VIẾT
                if (baiGiang.getLoaiBaiGiang() == LoaiBaiGiang.TAILIEU && baiGiang.getBaiViet() != null) {
                    BaiViet baiViet = baiGiang.getBaiViet();
                    BaiViet daCo = baivietService.findByBaiGiangId(baiGiangLuu.getBaiGiangId());
                    if (daCo != null) {
                        daCo.setNoidung(baiViet.getNoidung());
                        baivietService.save(daCo);
                    } else {
                        baiViet.setBaiGiang(baiGiangLuu);
                        baivietService.save(baiViet);
                    }
                }

                redirectAttributes.addFlashAttribute("thongbao", "Lưu bài giảng thành công.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("loi", "Lỗi khi lưu bài giảng.");
                e.printStackTrace();
            }
        }

        redirectAttributes.addAttribute("khoahocId", khoahocId);
        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
    }

    @PostMapping("/giangvien/them-moi-khoa-hoc/luu-chuong-va-baigiang")
    public String saveChuongVaBaiGiang(@ModelAttribute("chuongForm") ChuongFormWrapper wrapper,
            @RequestParam("khoahocId") Integer khoahocId,
            Model model,
            RedirectAttributes redirectAttributes) {

        List<String> danhSachLoi = kiemTraLoiDuLieu(wrapper);

        if (!danhSachLoi.isEmpty()) {
            StringBuilder thongBaoLoi = new StringBuilder();
            for (String loi : danhSachLoi) {
                thongBaoLoi.append(loi).append("<br>");
            }

            model.addAttribute("khoahoc", khoaHocService.getKhoaHocById(khoahocId));
            model.addAttribute("chuongForm", wrapper);
            model.addAttribute("loidinhdang", thongBaoLoi.toString());
            return "views/gdienGiangVien/them-chuong-va-bai-giang";
        }

        KhoaHoc khoaHoc = khoaHocService.getKhoaHocById(khoahocId);
        int thuTu = 1;

        for (Chuong chuong : wrapper.getChuongs()) {
            chuong.setKhoahoc(khoaHoc);
            chuong.setThutuchuong(thuTu++);

            Chuong chuongDaCo = null;
            if (chuong.getChuongId() != null) {
                chuongDaCo = chuongService.findById(chuong.getChuongId());
                if (chuongDaCo != null) {
                    chuongDaCo.setTenchuong(chuong.getTenchuong());
                    chuongDaCo.setMota(chuong.getMota());
                    chuongDaCo.setThutuchuong(chuong.getThutuchuong());
                }
            }

            Chuong chuongLuu = (chuongDaCo != null) ? chuongService.save(chuongDaCo) : chuongService.save(chuong);

            for (BaiGiang bg : chuong.getBaiGiangs()) {
                bg.setChuong(chuongLuu);
                bg.setTrangthai(true);

                BaiGiang baiGiangLuu;

                if (bg.getBaiGiangId() != null) {
                    BaiGiang bgCu = baiGiangService.findBaiGiangById(bg.getBaiGiangId());
                    if (daThayDoiLoaiBaiGiang(bg, bgCu)) {
                        redirectAttributes.addFlashAttribute("loi",
                                "❌ Không được thay đổi loại bài giảng đã lưu: " + bg.getTenBaiGiang());
                        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
                    }
                    if (bgCu != null) {
                        bgCu.setTenBaiGiang(bg.getTenBaiGiang());
                        bgCu.setLoaiBaiGiang(bg.getLoaiBaiGiang());
                        bgCu.setMota(bg.getMota());
                        bgCu.setChuong(chuongLuu);
                        baiGiangLuu = baiGiangService.save(bgCu);
                    } else {
                        baiGiangLuu = baiGiangService.save(bg);
                    }
                } else {
                    baiGiangLuu = baiGiangService.save(bg);
                }

                // VIDEO
                try {

                    if (bg.getLoaiBaiGiang() == LoaiBaiGiang.VIDEO && bg.getVideoBaiGiang() != null) {
                        VideoBaiGiang newVideo = bg.getVideoBaiGiang();

                        VideoBaiGiang oldVideo = videoBaiGiangService.findByBaiGiangId(baiGiangLuu.getBaiGiangId());

                        if (oldVideo != null) {

                            oldVideo.setUrl_video(newVideo.getUrl_video());
                            oldVideo.setMota(newVideo.getMota());
                            oldVideo.setNgayCapNhat(LocalDateTime.now());
                            videoBaiGiangService.save(oldVideo);
                        } else {
                            newVideo.setBaiGiang(baiGiangLuu);
                            videoBaiGiangService.save(newVideo);
                        }
                    }
                } catch (IllegalStateException e) {
                    redirectAttributes.addFlashAttribute("loi", "❌ " + e.getMessage());
                    return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
                }

                // 📄 BÀI VIẾT
                try {

                    if (bg.getLoaiBaiGiang() == LoaiBaiGiang.TAILIEU && bg.getBaiViet() != null) {
                        BaiViet baiViet = bg.getBaiViet();
                        baiViet.setBaiGiang(baiGiangLuu);

                        if (baiViet.getBaivietId() == null) {

                            BaiViet daCo = baivietService.findByBaiGiangId(baiGiangLuu.getBaiGiangId());
                            if (daCo != null) {
                                daCo.setNoidung(baiViet.getNoidung());
                                baivietService.save(daCo);
                            } else {
                                baivietService.save(baiViet);
                            }
                        } else {
                            BaiViet baiVietCu = baivietService.findById(baiViet.getBaivietId());
                            if (baiVietCu != null) {
                                baiVietCu.setNoidung(baiViet.getNoidung());
                                baiVietCu.setBaiGiang(baiGiangLuu);
                                baivietService.save(baiVietCu);
                            }
                        }
                    }
                } catch (IllegalStateException e) {
                    redirectAttributes.addFlashAttribute("loi", "❌ " + e.getMessage());
                    return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
                }

                // trắc nghiệm
                try {

                    if (bg.getLoaiBaiGiang() == LoaiBaiGiang.TRACNGHIEM && bg.getTracNghiem() != null) {
                        BaiTracNghiem tracMoi = bg.getTracNghiem();
                        baitracNghiemService.saveBaiTracNghiemVaCauHoi(tracMoi, baiGiangLuu);
                    }
                } catch (IllegalStateException e) {
                    redirectAttributes.addFlashAttribute("loi", "❌ " + e.getMessage());
                    return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
                }

            }
        }

        redirectAttributes.addFlashAttribute("thongbao", "✔ Đã lưu thông tin khóa học của bạn.");

        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
    }

    @PostMapping("/giangvien/them-moi-khoa-hoc/xoa-chuong")
    public String xoaChuong(
            @RequestParam("chuongId") Integer chuongId,
            @RequestParam("khoahocId") Integer khoahocId) {

        Chuong chuongCanXoa = chuongService.findById(chuongId);
        int thuTuBiXoa = chuongCanXoa.getThutuchuong();

        chuongService.deleteById(chuongId);

        List<Chuong> chuongsCanCapNhat = chuongService
                .findByKhoahocIdAndThutuchuongGreaterThanOrderByThutuchuongAsc(khoahocId, thuTuBiXoa);

        for (Chuong c : chuongsCanCapNhat) {
            c.setThutuchuong(c.getThutuchuong() - 1);
            chuongService.save(c);
        }

        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
    }

    @PostMapping("/giangvien/them-moi-khoa-hoc/xoa-baigiang")
    public String xoaBaiGiang(@RequestParam("baiGiangId") Integer baiGiangId,
            @RequestParam("khoahocId") Integer khoahocId,
            RedirectAttributes redirectAttributes) {
        try {
            baiGiangService.xoaBaiGiangTheoId(baiGiangId);
            redirectAttributes.addFlashAttribute("thongbao", "✔ Đã xóa: bài giảng.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "⚠ Xóa: bài giảng thất bại: " + e.getMessage());
        }
        return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId=" + khoahocId;
    }

    private List<String> kiemTraLoiDuLieu(ChuongFormWrapper wrapper) {
        List<String> danhSachLoi = new ArrayList<>();

        for (Chuong chuong : wrapper.getChuongs()) {
            if (chuong.getTenchuong() == null || chuong.getTenchuong().trim().isEmpty()) {
                danhSachLoi.add("⚠ Tên chương không được để trống.");
            } else if (chuong.getTenchuong() == null || chuong.getTenchuong().trim().length() < 5) {
                danhSachLoi.add("⚠ Tên chương học phải có ít nhất 5 ký tự.");
            }

            for (BaiGiang bg : chuong.getBaiGiangs()) {
                if (bg.getTenBaiGiang() == null || bg.getTenBaiGiang().trim().isEmpty()) {
                    danhSachLoi.add("⚠ Tên bài giảng không được để trống.");
                } else if (bg.getTenBaiGiang() == null || bg.getTenBaiGiang().trim().length() < 5) {
                    danhSachLoi.add("⚠ Tên bài giảng phải có ít nhất 5 ký tự.");
                }

                if (bg.getMota() == null || bg.getMota().trim().isEmpty()) {
                    danhSachLoi.add("⚠ Nội dung mô tả bài giảng không được để trống.");
                } else if (bg.getMota() == null || bg.getMota().trim().length() < 10) {
                    danhSachLoi.add("⚠ Mô tả bài giảng phải có ít nhất 10 ký tự.");
                }

                if (bg.getLoaiBaiGiang() == LoaiBaiGiang.VIDEO && bg.getVideoBaiGiang() != null) {
                    String url = bg.getVideoBaiGiang().getUrl_video();

                    if (url == null || url.trim().isEmpty()) {
                        danhSachLoi.add("⚠ URL video không được để trống.");
                    } else if (url == null || url.trim().length() < 10) {
                        danhSachLoi.add("⚠ URL video không hợp lệ - phải có ít nhất 10 ký tự.");
                    }

                } else if (bg.getLoaiBaiGiang() == LoaiBaiGiang.TAILIEU && bg.getBaiViet() != null) {
                    String noidung = bg.getBaiViet().getNoidung();

                    if (noidung == null || noidung.trim().isEmpty()) {
                        danhSachLoi.add("⚠ Nội dung của tài liệu không được để trống.");
                    } else if (noidung == null || noidung.trim().length() < 20) {
                        danhSachLoi.add("⚠ Nội dung tài liệu phải có ít nhất 20 ký tự.");
                    }

                }
            }
        }

        return danhSachLoi;
    }

    private boolean daThayDoiLoaiBaiGiang(BaiGiang moi, BaiGiang cu) {
        return cu != null && cu.getLoaiBaiGiang() != null
                && moi.getLoaiBaiGiang() != null
                && !moi.getLoaiBaiGiang().equals(cu.getLoaiBaiGiang());
    }

    @PostMapping("/khoa-hoc/yeu-cau-duyet")
    @ResponseBody
    public ResponseEntity<String> yeuCauDuyet(@RequestParam("id") Integer khoaHocId) {
        try {
            Optional<KhoaHoc> opt = khoaHocService.findById(khoaHocId);
            if (opt.isPresent()) {
                KhoaHoc kh = opt.get();
                kh.setTrangThai(TrangThaiKhoaHoc.PENDING_APPROVAL);
                khoaHocService.save(kh);
                return ResponseEntity.ok("Yêu cầu duyệt đã được gửi thành công.");
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy khóa học.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi gửi yêu cầu duyệt.");
        }
    }

}
