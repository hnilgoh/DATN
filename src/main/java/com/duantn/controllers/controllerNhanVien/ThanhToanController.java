// package com.duantn.controllers.controllerNhanVien;

// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;

// import java.math.BigDecimal;
// import java.util.Arrays;
// import java.util.List;

// @Controller
// @RequestMapping({ "/admin", "/nhanvien" })
// @PreAuthorize("hasAnyRole('ADMIN', 'NHANVIEN')")
// public class ThanhToanController {

// public static class ThanhToanVM {
// public Integer id;
// public String tenHocVien;
// public String email;
// public String ngayThanhToan;
// public String hinhThuc;
// public BigDecimal tongTien;
// public String trangThai;
// public String tenKhoaHoc;
// public String maGiaoDich;
// public String ghiChu;

// public ThanhToanVM(Integer id, String tenHocVien, String email, String
// ngayThanhToan, String hinhThuc,
// BigDecimal tongTien, String trangThai, String tenKhoaHoc, String maGiaoDich,
// String ghiChu) {
// this.id = id;
// this.tenHocVien = tenHocVien;
// this.email = email;
// this.ngayThanhToan = ngayThanhToan;
// this.hinhThuc = hinhThuc;
// this.tongTien = tongTien;
// this.trangThai = trangThai;
// this.tenKhoaHoc = tenKhoaHoc;
// this.maGiaoDich = maGiaoDich;
// this.ghiChu = ghiChu;
// }
// }

// private List<ThanhToanVM> listThanhToan() {
// return Arrays.asList(
// new ThanhToanVM(1, "Nguyễn Văn A", "a@gmail.com", "01/06/2024 10:00",
// "VNPAY",
// new BigDecimal("1500000"), "THANH_CONG", "Java Cơ bản", "GD001", "Thanh toán
// thành công"),
// new ThanhToanVM(2, "Trần Thị B", "b@gmail.com", "02/06/2024 14:30", "MOMO",
// new BigDecimal("1200000"),
// "DANG_CHO", "Spring Boot Mastery", "GD002", "Chờ xác nhận"),
// new ThanhToanVM(3, "Lê Văn C", "c@gmail.com", "03/06/2024 09:15", "VNPAY",
// new BigDecimal("900000"),
// "THAT_BAI", "ReactJS từ A-Z", "GD003", "Lỗi giao dịch"));
// }

// @GetMapping("/quanly-thanh-toan")
// public String listThanhToan(Model model) {
// model.addAttribute("listThanhToan", listThanhToan());
// return "views/gdienQuanLy/thanhtoan";
// }

// @GetMapping("/quanly-thanh-toan/{id}")
// public String detailThanhToan(@PathVariable Integer id, Model model) {
// ThanhToanVM detail = null;
// for (ThanhToanVM tt : listThanhToan()) {
// if (tt.id.equals(id)) {
// detail = tt;
// break;
// }
// }
// model.addAttribute("tt", detail);
// return "views/gdienQuanLy/thanhToanDetail";
// }
// }