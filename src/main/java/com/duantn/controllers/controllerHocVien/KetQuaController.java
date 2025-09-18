package com.duantn.controllers.controllerHocVien;

import com.duantn.dtos.KetQuaRequestDto;
import com.duantn.entities.BaiGiang;
import com.duantn.entities.DangHoc;
import com.duantn.entities.KetQua;
import com.duantn.services.BaiGiangService;
import com.duantn.services.DangHocService;
import com.duantn.services.KetQuaService;
import com.duantn.services.TienDoHocService;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ket-qua")
public class KetQuaController {

    @Autowired
    private KetQuaService ketQuaService;

    @Autowired
    private DangHocService dangHocService;

    @Autowired
    private BaiGiangService baiGiangService;

    @Autowired
    private TienDoHocService tienDoHocService;

    @GetMapping("/chi-tiet/{ketQuaId}")
    @ResponseBody
    public ResponseEntity<?> xemChiTiet(@PathVariable Integer ketQuaId) {
        return ResponseEntity.ok(ketQuaService.layChiTietTheoKetQuaId(ketQuaId));
    }

    @PostMapping("/luu")
    @ResponseBody
    public ResponseEntity<?> luuKetQua(@RequestBody KetQuaRequestDto dto) {
        KetQua kq = ketQuaService.luuKetQua(dto);

        BaiGiang baiGiang = baiGiangService.findByTracNghiemId(dto.getBaiTracNghiemId());
        if (baiGiang != null && baiGiang.getChuong() != null) {
            Integer khoaHocId = baiGiang.getChuong().getKhoahoc().getKhoahocId();
            DangHoc dangHoc = dangHocService.findByTaiKhoanIdAndKhoaHocId(dto.getTaiKhoanId(), khoaHocId);
            if (dangHoc != null) {
                tienDoHocService.capNhatTienDoSauKhiHoc(dto.getTaiKhoanId(), baiGiang.getBaiGiangId());
            }
        }
        return ResponseEntity.ok(kq.getKetquaId());
    }

    @GetMapping("/kiemtra")
    public ResponseEntity<?> kiemTraKetQua(
            @RequestParam Integer taiKhoanId,
            @RequestParam Integer baiTracNghiemId) {

        Optional<KetQua> ketQuaOpt = ketQuaService.findByTaiKhoanIdAndTracNghiemId(taiKhoanId, baiTracNghiemId);

        if (ketQuaOpt.isPresent()) {
            KetQua ketQua = ketQuaOpt.get();
            return ResponseEntity.ok(Map.of(
                    "daLam", true,
                    "ketQuaId", ketQua.getKetquaId(),
                    "soCauDung", ketQua.getSoCauDung(),
                    "tongDiem", ketQua.getTongDiem(),
                    "tongCauHoi", ketQua.getKetQuaChiTiet().size(),
                    "thoiGianBatDau",
                    ketQua.getThoiGianBatDau().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")),
                    "thoiGianKetThuc",
                    ketQua.getThoiGianKetThuc().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));
        } else {
            return ResponseEntity.ok(Map.of("daLam", false));
        }
    }

}
