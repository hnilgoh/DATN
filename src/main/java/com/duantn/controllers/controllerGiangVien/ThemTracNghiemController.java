package com.duantn.controllers.controllerGiangVien;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.duantn.dtos.BaiTracNghiemDto;
import com.duantn.dtos.CauHoiDTO;
import com.duantn.dtos.DapAnDto;
import com.duantn.entities.BaiGiang;
import com.duantn.entities.BaiTracNghiem;
import com.duantn.entities.CauHoi;
import com.duantn.entities.DapAn;
import com.duantn.enums.LoaiBaiGiang;
import com.duantn.services.BaiGiangService;
import com.duantn.services.BaiTracNghiemService;
import com.duantn.services.CauHoiService;

@Controller
public class ThemTracNghiemController {
    @Autowired
    BaiGiangService bGiangService;

    @Autowired
    BaiTracNghiemService baiTracNghiemService;

    @Autowired
    CauHoiService cauHoiService;

    @GetMapping("/giangvien/cau-hoi/them")
    public String showThemCauHoi(@RequestParam("baiGiangId") Integer baiGiangId, Model model,
            RedirectAttributes redirectAttributes) {
        BaiGiang baiGiang = bGiangService.findBaiGiangById(baiGiangId);

        if (baiGiang == null) {
            return "redirect:/giangvien/khoahoc";
        }

        damBaoLoaiTracNghiem(baiGiang); // ✅ gán loại nếu chưa có

        if (baiGiang.getLoaiBaiGiang() != LoaiBaiGiang.TRACNGHIEM) {
            redirectAttributes.addFlashAttribute("loi",
                    "❌ Bài giảng đã có nội dung khác (video hoặc bài viết). Vui lòng xóa trước khi thêm trắc nghiệm.");
            return "redirect:/giangvien/them-moi-khoa-hoc/them-chuong?khoahocId="
                    + baiGiang.getChuong().getKhoahoc().getKhoahocId();
        }

        BaiTracNghiem tracNghiem = baiTracNghiemService.findFullByBaiGiangId(baiGiangId);
        if (tracNghiem == null) {
            tracNghiem = new BaiTracNghiem();
            tracNghiem.setBaiGiang(baiGiang);
            tracNghiem = baiTracNghiemService.save(tracNghiem); // BẮT BUỘC PHẢI LƯU
            tracNghiem.setCauHoiList(new ArrayList<>());
        }

        if (tracNghiem.getCauHoiList().isEmpty()) {
            tracNghiem.getCauHoiList().add(taoCauHoiTrong());
        }

        model.addAttribute("baiGiang", baiGiang);
        model.addAttribute("tracNghiem", tracNghiem);

        return "views/gdienGiangVien/them-trac-nghiem";
    }

    private CauHoi taoCauHoiTrong() {
        CauHoi ch = new CauHoi();
        List<DapAn> ds = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            ds.add(new DapAn());
        ch.setDapAnList(ds);
        return ch;
    }

    @PostMapping(value = "/giangvien/cau-hoi/luu-nhanh", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> luuNhanhCauHoi(@RequestBody CauHoiDTO dto) {

        if (dto.getTenCauHoi() == null || dto.getTenCauHoi().isBlank()) {
            return ResponseEntity.badRequest().body("Tên câu hỏi không được để trống");
        }

        // ---- Lấy / tạo Bài Trắc Nghiệm ----
        BaiTracNghiem trac = baiTracNghiemService.findByBaiGiangId(dto.getBaiGiangId());
        if (trac == null) { // CHƯA CÓ -> tự động tạo mới
            BaiGiang bg = bGiangService.findBaiGiangById(dto.getBaiGiangId());
            if (bg == null)
                return ResponseEntity.badRequest().body("Sai baiGiangId");

            trac = new BaiTracNghiem();
            trac.setBaiGiang(bg);
            trac.setTenbai("Trắc nghiệm của " + bg.getTenBaiGiang());
            trac = baiTracNghiemService.save(trac);
        }

        // ---- Lấy / tạo Câu Hỏi ----
        CauHoi cauHoi = (dto.getCauHoiId() != null)
                ? cauHoiService.findById(dto.getCauHoiId())
                : new CauHoi();

        if (cauHoi == null) // id gửi lên nhưng không tìm thấy
            cauHoi = new CauHoi();

        cauHoi.setBaiTracNghiem(trac);
        cauHoi.setTenCauHoi(dto.getTenCauHoi());
        cauHoi.setNgayCapNhat(LocalDateTime.now());

        // nếu mới -> tạo sẵn 4 đáp án rỗng
        if (cauHoi.getCauHoiId() == null) {
            List<DapAn> list = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                DapAn da = new DapAn();
                da.setThuTuDapAn(i + 1);
                da.setCauHoi(cauHoi);
                list.add(da);
            }
            cauHoi.setDapAnList(list);
        }

        CauHoi saved = cauHoiService.save(cauHoi);
        return ResponseEntity.ok(Map.of("cauHoiId", saved.getCauHoiId()));
    }

    @PostMapping("/giangvien/cau-hoi/xu-ly")
    public String xuLyCauHoi(
            @RequestParam Integer baiGiangId,
            @RequestParam String action,
            @RequestParam String tenbai,
            @ModelAttribute("tracNghiem") BaiTracNghiemDto tracNghiemDTO,
            Model model,
            RedirectAttributes redirect) {

        BaiGiang baiGiang = bGiangService.findBaiGiangById(baiGiangId);
        if (baiGiang == null) {
            redirect.addFlashAttribute("error", "Không tìm thấy bài giảng.");
            return "redirect:/giangvien/khoahoc";
        }

        BaiTracNghiem trac = baiTracNghiemService.findByBaiGiangId(baiGiangId);
        if (trac == null) {
            trac = new BaiTracNghiem();
            trac.setBaiGiang(baiGiang);
        }
        trac.setTenbai(tenbai);
        trac = baiTracNghiemService.save(trac);

        if ("themCauHoi".equals(action)) {
            for (CauHoiDTO ch : tracNghiemDTO.getCauHoiList()) {
                Integer dung = ch.getDapAnDungIndex();
                if (dung != null && ch.getDapAnList() != null
                        && dung >= 0 && dung < ch.getDapAnList().size()) {
                    for (int i = 0; i < ch.getDapAnList().size(); i++) {
                        ch.getDapAnList().get(i).setDapAnDung(i == dung);
                    }
                }
            }
            // Thêm 1 câu hỏi trống
            CauHoiDTO cauHoiMoi = new CauHoiDTO();
            List<DapAnDto> ds = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                ds.add(new DapAnDto());
            }
            cauHoiMoi.setDapAnList(ds);

            if (tracNghiemDTO.getCauHoiList() == null) {
                tracNghiemDTO.setCauHoiList(new ArrayList<>());
            }
            tracNghiemDTO.getCauHoiList().add(cauHoiMoi);

            model.addAttribute("baiGiang", baiGiang);
            model.addAttribute("tracNghiem", tracNghiemDTO);

            return "views/gdienGiangVien/them-trac-nghiem"; // Không redirect, giữ dữ liệu trên form
        }

        if ("luuToanBo".equals(action)) {
            int stt = 1;
            for (CauHoiDTO dto : tracNghiemDTO.getCauHoiList()) {
                if (dto.getTenCauHoi() == null || dto.getTenCauHoi().trim().isEmpty()) {
                    continue;
                }

                CauHoi cauHoi = (dto.getCauHoiId() != null)
                        ? cauHoiService.findById(dto.getCauHoiId())
                        : new CauHoi();

                if (cauHoi == null)
                    cauHoi = new CauHoi();

                cauHoi.setTenCauHoi(dto.getTenCauHoi());
                cauHoi.setTrangthai(true);
                cauHoi.setBaiTracNghiem(trac);
                cauHoi.setCauHoiSo(stt++);

                List<DapAn> listDapAn = new ArrayList<>();
                List<DapAnDto> dapAnDTOs = dto.getDapAnList();

                if (dapAnDTOs != null) {
                    for (int i = 0; i < dapAnDTOs.size(); i++) {
                        DapAnDto daDto = dapAnDTOs.get(i);
                        if (daDto.getNoiDungDapAn() == null || daDto.getNoiDungDapAn().trim().isEmpty())
                            continue;

                        DapAn da = new DapAn();
                        da.setNoiDungDapAn(daDto.getNoiDungDapAn());
                        da.setThuTuDapAn(i);
                        boolean laDapAnDung = Integer.valueOf(i).equals(dto.getDapAnDungIndex());
                        da.setDapAnDung(laDapAnDung);
                        da.setGiaThichDapan(i == dto.getDapAnDungIndex() ? dto.getGiaiThich() : null);
                        da.setCauHoi(cauHoi);
                        da.setTrangthai(true);

                        listDapAn.add(da);
                    }
                }

                // Cập nhật danh sách đáp án
                cauHoi.getDapAnList().clear();
                cauHoi.getDapAnList().addAll(listDapAn);

                cauHoiService.save(cauHoi);
            }

            redirect.addFlashAttribute("success", "✅ Đã lưu toàn bộ câu hỏi.");
            return "redirect:/giangvien/cau-hoi/them?baiGiangId=" + baiGiangId;
        }

        redirect.addFlashAttribute("error", "Không rõ hành động.");
        return "redirect:/giangvien/cau-hoi/them?baiGiangId=" + baiGiangId;
    }

    @PostMapping("/giangvien/tracnghiem/luu")
    public String luuTracNghiem(
            @RequestParam Integer baiGiangId,
            @RequestParam String tenbai,
            RedirectAttributes redirect) {

        BaiGiang baiGiang = bGiangService.findBaiGiangById(baiGiangId);
        if (baiGiang == null) {
            redirect.addFlashAttribute("error", "Không tìm thấy bài giảng.");
            return "redirect:/giangvien/khoahoc"; // hoặc đường dẫn fallback
        }

        BaiTracNghiem trac = baiTracNghiemService.findByBaiGiangId(baiGiangId);
        if (trac == null) {
            trac = new BaiTracNghiem();
            trac.setBaiGiang(baiGiang);
        }

        trac.setTenbai(tenbai);
        baiTracNghiemService.save(trac);

        redirect.addFlashAttribute("success", "✅ Đã lưu trắc nghiệm.");
        return "redirect:/giangvien/cau-hoi/them?baiGiangId=" + baiGiangId;
    }

    private void damBaoLoaiTracNghiem(BaiGiang bg) {
        if (bg.getLoaiBaiGiang() == null) {
            bg.setLoaiBaiGiang(LoaiBaiGiang.TRACNGHIEM);
            bGiangService.save(bg);
        }
    }
}
