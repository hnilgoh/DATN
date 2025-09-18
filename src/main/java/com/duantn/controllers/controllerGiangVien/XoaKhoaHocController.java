package com.duantn.controllers.controllerGiangVien;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.entities.KhoaHoc;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.services.DangHocService;
import com.duantn.services.KhoaHocService;

@RestController
public class XoaKhoaHocController {
    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private DangHocService dangHocService;

    @PostMapping("/giangvien/xoa-khoa-hoc/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> xoaKhoaHoc(@PathVariable("id") Integer khoaHocId) {
        Map<String, Object> response = new HashMap<>();

        KhoaHoc kh = khoaHocService.findById(khoaHocId).orElse(null);
        if (kh == null) {
            response.put("success", false);
            response.put("message", "Không tìm thấy khóa học.");
            return ResponseEntity.ok(response);
        }

        // Nếu có học viên → luôn chuyển sang UNPUBLISHED
        boolean coHocVien = dangHocService.existsByKhoaHocId(khoaHocId);
        if (coHocVien) {
            kh.setTrangThai(TrangThaiKhoaHoc.UNPUBLISHED);
            khoaHocService.save(kh);
            response.put("success", true);
            response.put("message", "⚠️ Khóa học đang có học viên đăng ký và sẽ không hiển thị trên trang chủ nữa.");
        } else {
            // Không có học viên → xử lý theo trạng thái hiện tại
            if (kh.getTrangThai() == TrangThaiKhoaHoc.PUBLISHED) {
                kh.setTrangThai(TrangThaiKhoaHoc.UNPUBLISHED);
                response.put("message", "✅ Khóa học đã được ẩn khỏi trang chủ.");
            } else if (kh.getTrangThai() == TrangThaiKhoaHoc.DRAFT) {
                kh.setTrangThai(TrangThaiKhoaHoc.HIDDEN);
                response.put("message", "✅ Khóa học nháp của bạn đã được xóa.");
            } else {
                response.put("message", "✅ Không thay đổi trạng thái vì không phù hợp với điều kiện.");
            }
            khoaHocService.save(kh);
            response.put("success", true);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/khoahoc/mo-lai/{id}")
    @ResponseBody
    public ResponseEntity<?> moLaiKhoaHoc(@PathVariable Integer id) {
        Optional<KhoaHoc> opt = khoaHocService.findById(id);
        if (opt.isEmpty())
            return ResponseEntity.notFound().build();

        KhoaHoc kh = opt.get();
        kh.setTrangThai(TrangThaiKhoaHoc.PUBLISHED);
        khoaHocService.save(kh);

        return ResponseEntity.ok().body("Khóa học đã được mở lại");
    }

}
