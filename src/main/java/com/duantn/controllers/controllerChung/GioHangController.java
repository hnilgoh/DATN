package com.duantn.controllers.controllerChung;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duantn.dtos.CartItem;
import com.duantn.entities.GioHang;
import com.duantn.entities.GioHangChiTiet;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.repositories.GioHangChiTietRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.services.DanhGiaService;
import com.duantn.services.GioHangService;
import com.duantn.services.TaiKhoanService;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/gio-hang")

public class GioHangController {

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private KhoaHocRepository khoaHocRepo;

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private GioHangChiTietRepository chiTietRepo;

    @Autowired
    private DanhGiaService danhGiaService;

    @RequestMapping()
    public String requestMethodName() {
        return "views/gdienChung/giohang";
    }

    @PostMapping("/them")
    public ResponseEntity<?> themKhoaHocVaoGio(@RequestBody CartItem item) {
        TaiKhoan user = taiKhoanService.getCurrentUser();
        GioHang gioHang = gioHangService.getOrCreateGioHang(user);

        KhoaHoc khoaHoc = khoaHocRepo.findById(item.getKhoaHocId()).orElse(null);
        if (khoaHoc == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy khóa học");
        }

        boolean exists = chiTietRepo.findByGiohangAndKhoahoc(gioHang, khoaHoc).isPresent();
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Khóa học đã có trong giỏ hàng");
        }

        GioHangChiTiet chiTiet = GioHangChiTiet.builder()
                .giohang(gioHang)
                .khoahoc(khoaHoc)
                .dongia(item.getGia())
                .build();
        chiTietRepo.save(chiTiet);

        return ResponseEntity.ok("Đã thêm vào giỏ hàng");
    }

    @PostMapping("/sync")
    @Transactional
    public ResponseEntity<?> syncCart(@RequestBody List<CartItem> items) {
        TaiKhoan user = taiKhoanService.getCurrentUser();
        GioHang gioHang = gioHangService.getOrCreateGioHang(user);

        for (CartItem item : items) {
            KhoaHoc khoaHoc = khoaHocRepo.findById(item.getKhoaHocId()).orElse(null);
            if (khoaHoc == null)
                continue;

            boolean exists = chiTietRepo.findByGiohangAndKhoahoc(gioHang, khoaHoc).isPresent();
            if (!exists) {
                GioHangChiTiet chiTiet = GioHangChiTiet.builder()
                        .giohang(gioHang)
                        .khoahoc(khoaHoc)
                        .dongia(item.getGia())
                        .build();
                chiTietRepo.save(chiTiet);
            }
        }

        return ResponseEntity.ok("Đã đồng bộ giỏ hàng!");
    }

    @GetMapping("/data")
    public ResponseEntity<?> getCartData() {
        TaiKhoan user = taiKhoanService.getCurrentUser();
        GioHang gioHang = gioHangService.getOrCreateGioHang(user);

        List<GioHangChiTiet> list = chiTietRepo.findByGiohang(gioHang);

        List<CartItem> result = list.stream().map(ct -> {
            KhoaHoc k = ct.getKhoahoc();

            long soLuongDanhGia = danhGiaService.demSoLuongDanhGia(k.getKhoahocId());
            double diemTrungBinh = danhGiaService.diemTrungBinh(k.getKhoahocId());

            return new CartItem(
                    k.getKhoahocId(),
                    k.getTenKhoaHoc(),
                    ct.getDongia(),
                    k.getGiagoc(),
                    k.getAnhBia(),
                    k.getGiangVien().getTaikhoan().getName(),
                    (int) soLuongDanhGia,
                    diemTrungBinh);
        }).toList();

        return ResponseEntity.ok(result);
    }

    // Xóa 1 mục khỏi DB nếu đã đồng bộ
    @DeleteMapping("/xoa/{khoaHocId}")
    public ResponseEntity<?> deleteItem(@PathVariable Integer khoaHocId) {
        try {
            TaiKhoan user = taiKhoanService.getCurrentUser();
            GioHang gioHang = gioHangService.getOrCreateGioHang(user);

            Optional<KhoaHoc> optKhoaHoc = khoaHocRepo.findById(khoaHocId);
            if (optKhoaHoc.isEmpty()) {
                return ResponseEntity.badRequest().body("Khóa học không tồn tại");
            }

            Optional<GioHangChiTiet> chiTiet = chiTietRepo.findByGiohangAndKhoahoc(gioHang, optKhoaHoc.get());
            chiTiet.ifPresent(chiTietRepo::delete);

            return ResponseEntity.ok("Đã xóa khỏi DB");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    @Transactional
    public ResponseEntity<?> clearCart() {
        try {
            TaiKhoan user = taiKhoanService.getCurrentUser();
            GioHang gioHang = gioHangService.getOrCreateGioHang(user);

            List<GioHangChiTiet> chiTietList = chiTietRepo.findByGiohang(gioHang);
            chiTietRepo.deleteAll(chiTietList);

            return ResponseEntity.ok("Đã xóa toàn bộ giỏ hàng");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa giỏ hàng: " + e.getMessage());
        }
    }

}