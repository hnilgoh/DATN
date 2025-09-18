package com.duantn.controllers.controllerHocVien;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.duantn.dtos.GiaoDichRequest;
import com.duantn.entities.DangHoc;
import com.duantn.entities.DoanhThuGiangVien;
import com.duantn.entities.GiaoDichKhoaHoc;
import com.duantn.entities.GiaoDichKhoaHocChiTiet;
import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.entities.ThongBao;
import com.duantn.entities.ThuNhapNenTang;
import com.duantn.enums.HinhThucThanhToan;
import com.duantn.enums.LoaiThongBao;
import com.duantn.enums.TrangThaiGiaoDich;
import com.duantn.repositories.DangHocRepository;
import com.duantn.repositories.DoanhThuGiangVienRepository;
import com.duantn.repositories.GiaoDichKhoaHocChiTietRepository;
import com.duantn.repositories.GiaoDichKhoaHocRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.repositories.ThongBaoRepository;
import com.duantn.repositories.ThuNhapNenTangRepository;
import com.duantn.services.AuthService;
import com.duantn.services.EmailThanhToanThanhCongService;
import com.duantn.services.KhoaHocService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/hoc-vien")
public class ThanhToanController {

    @Autowired
    private KhoaHocService khoaHocService;

    @Autowired
    private GiaoDichKhoaHocChiTietRepository giaoDichChiTietRepo;

    @Autowired
    private GiaoDichKhoaHocRepository giaoDichKhoaHocRepository;

    @Autowired
    private DangHocRepository dangHocRepository;

    @Autowired
    private KhoaHocRepository khoaHocRepo;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private DoanhThuGiangVienRepository doanhThuGiangVienRepository;

    @Autowired
    private EmailThanhToanThanhCongService emailThanhToanThanhCongService;

    @Autowired
    private ThuNhapNenTangRepository thuNhapNenTangRepository;

    @Autowired
    private AuthService authService;

    @GetMapping("/thanh-toan")
    public String hienThiThanhToan(@RequestParam("khoahocId") List<Integer> ids, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TaiKhoan tk = authService.getTaiKhoanFromAuth(auth);

        if (tk == null) {
            return "redirect:/auth/dangnhap";
        }

        List<KhoaHoc> dsKhoaHoc = khoaHocService.findAllByIds(ids);

        BigDecimal tongTien = dsKhoaHoc.stream()
                .map(KhoaHoc::getGiaHienTai)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lưu giao dịch sơ bộ
        GiaoDichKhoaHoc giaoDich = GiaoDichKhoaHoc.builder()
                .tongtien(tongTien)
                .tenhocvien(tk.getName())
                .taikhoan(tk)
                .trangthai(TrangThaiGiaoDich.CHO_XU_LY)
                .hinhThucThanhToan(HinhThucThanhToan.SmartBanking)
                .build();

        giaoDichKhoaHocRepository.save(giaoDich);

        // Truyền giaoDichId sang giao diện (nếu cần dùng lại)
        model.addAttribute("giaoDichId", giaoDich.getGiaodichId());

        List<Integer> khoaHocIds = dsKhoaHoc.stream()
                .map(KhoaHoc::getKhoahocId)
                .toList();

        model.addAttribute("khoaHocIds", khoaHocIds);
        model.addAttribute("dsKhoaHoc", dsKhoaHoc);
        model.addAttribute("tongTien", tongTien);

        return "views/gdienHocVien/thanh-toan";
    }

    @PostMapping("/api/thanh-toan/thanh-cong")
    public ResponseEntity<?> xuLyThanhToanThanhCong(@RequestBody GiaoDichRequest request) {
        Integer giaoDichId = Integer.valueOf(request.getGiaoDichId()); // hoặc parseInt()
        Optional<GiaoDichKhoaHoc> optionalGDKH = giaoDichKhoaHocRepository.findById(giaoDichId);

        if (optionalGDKH.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy giao dịch");
        }

        GiaoDichKhoaHoc giaoDichKH = optionalGDKH.get();

        // Cập nhật trạng thái giao dịch thành HOAN_THANH
        giaoDichKH.setTrangthai(TrangThaiGiaoDich.HOAN_THANH);
        giaoDichKhoaHocRepository.save(giaoDichKH);

        TaiKhoan taiKhoan = giaoDichKH.getTaikhoan(); // Lấy tài khoản từ giao dịch

        for (Integer khoaHocId : request.getKhoaHocIds()) {
            Optional<KhoaHoc> optionalKH = khoaHocRepo.findById(khoaHocId);
            if (optionalKH.isPresent()) {
                KhoaHoc kh = optionalKH.get();

                GiaoDichKhoaHocChiTiet chiTiet = new GiaoDichKhoaHocChiTiet();
                chiTiet.setGiaoDichKhoaHoc(giaoDichKH);
                chiTiet.setKhoahoc(kh);
                chiTiet.setDongia(kh.getGiaHienTai()); // hoặc kh.getGiaGoc()

                giaoDichChiTietRepo.save(chiTiet);

                // Tạo bản ghi vào bảng DangHoc
                DangHoc dangHoc = DangHoc.builder()
                        .taikhoan(taiKhoan)
                        .khoahoc(kh)
                        .dongia(kh.getGiaHienTai())
                        .trangthai(false)
                        .daCap_ChungChi(false)
                        .build();
                dangHocRepository.save(dangHoc);

                ThongBao thongBao = ThongBao.builder()
                        .tieuDe("✅ Thanh toán thành công khóa học: " + kh.getTenKhoaHoc())
                        .noiDung("🎉 Khóa học \"" + kh.getTenKhoaHoc()
                                + "\" đã được thêm vào danh sách khóa học của bạn. "
                                + "Bạn có thể bắt đầu học ngay hoặc xem chi tiết khóa học tại "
                                + "<a href='/hoc-vien/hoc-tap?tab=hoc-tap'>đây</a>.")
                        .loaiThongBao(LoaiThongBao.THONG_BAO_CHUNG)
                        .nguoiNhan(List.of(taiKhoan))
                        .build();

                thongBaoRepository.save(thongBao);

                // ➕ Sau khi đã lưu DangHoc, lưu luôn DoanhThu
                BigDecimal tiLeHoaHong = BigDecimal.valueOf(0.7); // 70% hoa hồng
                BigDecimal tienNhan = kh.getGiaHienTai().multiply(tiLeHoaHong); // Tính tiền giảng viên nhận được

                DoanhThuGiangVien doanhThu = DoanhThuGiangVien.builder()
                        .sotiennhan(tienNhan) // Tiền sẽ trả cho giảng viên
                        .tenGiangVien(kh.getGiangVien().getTaikhoan().getName()) // Lưu tên giảng viên để hiển thị
                        .taikhoanGV(kh.getGiangVien().getTaikhoan()) // Liên kết tới tài khoản giảng viên
                        .dangHoc(dangHoc) // Gắn với học viên đang học khóa đó
                        .build();

                doanhThuGiangVienRepository.save(doanhThu); // Lưu vào DB

                // ✅ Tính phần thu nhập nền tảng (30%)
                BigDecimal tiLeNenTang = BigDecimal.valueOf(0.3);
                BigDecimal tienNenTang = kh.getGiaHienTai().multiply(tiLeNenTang);

                // ✅ Tạo bản ghi thu nhập nền tảng
                ThuNhapNenTang thuNhapNenTang = ThuNhapNenTang.builder()
                        .sotiennhan(tienNenTang)
                        .dangHoc(dangHoc)
                        .tenKhoaHoc(kh.getTenKhoaHoc())
                        .thuocGiangVien(kh.getGiangVien().getTaikhoan().getName())
                        .build();

                thuNhapNenTangRepository.save(thuNhapNenTang); // ✅ Lưu vào DB

            }
        }

        // Lấy danh sách khóa học từ danh sách ID gửi lên
        List<KhoaHoc> dsKhoaHoc = khoaHocService.findAllByIds(request.getKhoaHocIds());
        // Gửi email xác nhận
        emailThanhToanThanhCongService.sendPaymentSuccessEmail(
                taiKhoan.getEmail(),
                taiKhoan.getName(),
                giaoDichKH.getGiaodichId().toString(),
                giaoDichKH.getTongtien().toPlainString(),
                dsKhoaHoc // List<KhoaHoc>
        );

        return ResponseEntity.ok("Đã lưu chi tiết giao dịch thành công");
    }

    @PostMapping("/api/thanh-toan/that-bai")
    public ResponseEntity<?> xuLyThanhToanThatBai(@RequestBody GiaoDichRequest request) {
        Integer giaoDichId = Integer.valueOf(request.getGiaoDichId());
        Optional<GiaoDichKhoaHoc> optionalGDKH = giaoDichKhoaHocRepository.findById(giaoDichId);

        if (optionalGDKH.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy giao dịch");
        }

        GiaoDichKhoaHoc giaoDichKH = optionalGDKH.get();

        // Cập nhật trạng thái giao dịch thành THAT_BAI
        giaoDichKH.setTrangthai(TrangThaiGiaoDich.THAT_BAI);
        giaoDichKhoaHocRepository.save(giaoDichKH);

        return ResponseEntity.ok("Đã cập nhật trạng thái thất bại cho giao dịch");
    }

}