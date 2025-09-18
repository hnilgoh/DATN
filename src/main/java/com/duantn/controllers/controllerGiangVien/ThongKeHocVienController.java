package com.duantn.controllers.controllerGiangVien;

import com.duantn.entities.KhoaHoc;
import com.duantn.services.DangHocService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hocvien")
@RequiredArgsConstructor
public class ThongKeHocVienController {

    private final DangHocService dangHocService;

    @GetMapping("/{hocVienId}/khoahoc")
    public ResponseEntity<List<KhoaHoc>> getKhoaHocByHocVien(@PathVariable Integer hocVienId) {
        List<KhoaHoc> dsKhoaHoc = dangHocService.findKhoaHocByHocVienId(hocVienId);
        return ResponseEntity.ok(dsKhoaHoc);
    }
}
