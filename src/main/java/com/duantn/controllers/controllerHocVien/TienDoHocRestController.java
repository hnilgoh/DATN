package com.duantn.controllers.controllerHocVien;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.dtos.TienDoHocRequest;
import com.duantn.entities.TaiKhoan;
import com.duantn.services.TaiKhoanService;
import com.duantn.services.TienDoHocService;

@RestController
@RequestMapping("/khoa-hoc/api/tien-do-hoc")
public class TienDoHocRestController {

    @Autowired
    private TienDoHocService tienDoHocService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @PostMapping("/cap-nhat")
    public ResponseEntity<?> capNhatTienDoHoc(@RequestBody TienDoHocRequest request, Principal principal) {
        String username = principal.getName();
        TaiKhoan tk = taiKhoanService.findByEmail(username);
        if (tk != null && request.getBaiGiangId() != null && request.getKhoaHocId() != null) {
            tienDoHocService.capNhatTienDoSauKhiHoc(tk.getTaikhoanId(), request.getBaiGiangId());
            return ResponseEntity.ok(Map.of("status", "success"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin hoặc chưa đăng nhập"));
    }

    @GetMapping("/phan-tram")
    public ResponseEntity<?> layTienDoPhanTram(@RequestParam Integer khoaHocId, Principal principal) {
        String username = principal.getName();
        TaiKhoan tk = taiKhoanService.findByEmail(username);
        if (tk != null) {
            int tienDo = tienDoHocService.tinhTienDoPhanTram(tk.getTaikhoanId(), khoaHocId);
            return ResponseEntity.ok(Map.of("phanTram", tienDo));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Thiếu thông tin hoặc chưa đăng nhập"));
    }

}
