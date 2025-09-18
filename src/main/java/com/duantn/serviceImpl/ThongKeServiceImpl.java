package com.duantn.serviceImpl;

import com.duantn.services.ThongKeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.duantn.entities.KhoaHoc;
import com.duantn.entities.TaiKhoan;
import com.duantn.enums.TrangThaiKhoaHoc;
import com.duantn.repositories.*;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

@Service
public class ThongKeServiceImpl implements ThongKeService {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private GiangVienRepository giangVienRepository;
    @Autowired
    private KhoaHocRepository khoaHocRepository;
    @Autowired
    private GiaoDichKhoaHocRepository giaoDichKhoaHocRepository;
    @Autowired
    private DangHocRepository dangHocRepository;
    @Autowired
    private ThuNhapNenTangRepository thuNhapNenTangRepository;
    @Autowired
    private DoanhThuGiangVienRepository doanhthurep;

    @Override
    public int tongHocVien() {
        return taiKhoanRepository.countHocVien();
    }

    @Override
    public int tongGiangVien() {
        return giangVienRepository.countGiangVien();
    }

    @Override
    public int countKhoaHocByTrangThai(TrangThaiKhoaHoc trangThai) {
        return khoaHocRepository.countKhoaHocByTrangThai(trangThai);
    }

    @Override
    public double doanhThuThangNay() {
        Double result = giaoDichKhoaHocRepository.doanhThuThangNay();
        return result != null ? result : 0.0;
    }

    @Override
    public List<Double> getDoanhThu6Thang() {
        LocalDateTime startDate = LocalDate.now().minusMonths(5).withDayOfMonth(1).atStartOfDay();
        List<Object[]> raw = giaoDichKhoaHocRepository.doanhThu6ThangGanNhat(startDate);
        Map<Integer, Double> monthToRevenue = new HashMap<>();
        for (Object[] row : raw) {
            monthToRevenue.put(((Integer) row[0]), row[1] != null ? ((BigDecimal) row[1]).doubleValue() : 0.0);
        }
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int month = LocalDate.now().minusMonths(5 - i).getMonthValue();
            result.add(monthToRevenue.getOrDefault(month, 0.0));
        }
        return result;
    }

    @Override
    public Map<String, Integer> getTiLeDanhMuc() {
        List<Object[]> raw = khoaHocRepository.tiLeDanhMuc();
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Object[] row : raw) {
            result.put((String) row[0], ((Long) row[1]).intValue());
        }
        return result;
    }

    @Override
    public List<Object> getChiTietKhoaHoc() {
        return new ArrayList<>(khoaHocRepository.chiTietKhoaHoc());
    }

    @Override
    public double tongTienNenTang() {
        Double result = thuNhapNenTangRepository.tongTienNenTang();
        return result != null ? result : 0.0;
    }

    @Override
    public int tongNhanVien() {
        return taiKhoanRepository.countNhanVien();
    }

    @Override
    public List<String> getTopKhoaHocLabels() {
        List<Object[]> results = dangHocRepository.findTop5KhoaHoc(PageRequest.of(0, 5));
        List<String> labels = new ArrayList<>();
        for (Object[] row : results) {
            labels.add((String) row[0]);
        }
        return labels;
    }

    @Override
    public List<Long> getTopKhoaHocSoLuong() {
        List<Object[]> results = dangHocRepository.findTop5KhoaHoc(PageRequest.of(0, 5));
        List<Long> soLuong = new ArrayList<>();
        for (Object[] row : results) {
            soLuong.add((Long) row[1]);
        }
        return soLuong;
    }

    @Override
    public Map<String, Long> thongKeTaiKhoanTheoVaiTro() {
        List<Object[]> data = taiKhoanRepository.countUsersByRole();
        Map<String, Long> result = new HashMap<>();

        for (Object[] row : data) {
            String role = (String) row[0];
            Long count = (Long) row[1];

            // Chỉ lấy học viên và giảng viên
            if ("ROLE_HOCVIEN".equals(role)) {
                result.put("Học viên", count);
            } else if ("ROLE_GIANGVIEN".equals(role)) {
                result.put("Giảng viên", count);
            }
        }

        return result;
    }

    @Override
    public Map<String, BigDecimal> getDoanhThuTheo6ThangGanNhat() {
        Map<String, BigDecimal> map = new LinkedHashMap<>();

        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            String label = "Tháng " + ym.getMonthValue();
            BigDecimal doanhThu = thuNhapNenTangRepository
                    .tongDoanhThuTheoThang(ym.getYear(), ym.getMonthValue());
            map.put(label, doanhThu != null ? doanhThu : BigDecimal.ZERO);
        }

        return map;
    }

    @Override
    public List<Object[]> getTop5DanhMuc() {
        return khoaHocRepository.findTopDanhMucBySoLuongKhoaHoc(PageRequest.of(0, 5));
    }

    @Override
    public int countHocVienDaDangKy() {
        return dangHocRepository.countHocVienDaDangKy();
    }

    @Override
    public List<KhoaHoc> getAllKhoaHocDaXuatBan() {
        return khoaHocRepository.findByTrangThai(TrangThaiKhoaHoc.PUBLISHED);
    }

    @Override
    public List<Object[]> getTop3GiangVienDoanhThu() {
        return doanhthurep.findTop3GiangVienDoanhThu(PageRequest.of(0, 3));
    }

    @Override
    public List<Object[]> getTop5GiangVienHocVien() {
        return dangHocRepository.findTop5GiangVienHocVien(PageRequest.of(0, 5));
    }

    @Override
    public Map<Integer, Double> layDoanhThuTheoThang(TaiKhoan giangVien) {
        List<Object[]> results = doanhthurep.thongKeDoanhThuTheoThang(giangVien);
        Map<Integer, Double> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            Integer thang = ((Number) row[0]).intValue();
            Double tong = ((Number) row[1]).doubleValue();
            map.put(thang, tong);
        }
        return map;
    }
}