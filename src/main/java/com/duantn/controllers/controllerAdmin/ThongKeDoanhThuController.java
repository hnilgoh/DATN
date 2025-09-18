package com.duantn.controllers.controllerAdmin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duantn.entities.ThuNhapNenTang;
import com.duantn.repositories.ThuNhapNenTangRepository;

@Controller
@RequestMapping("/admin/doanhthu")
public class ThongKeDoanhThuController {

    @Autowired
    private ThuNhapNenTangRepository thuNhapRepo;

    @GetMapping
    public String hienThiDoanhThu(
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(name = "quy", required = false) Integer quy,
            @RequestParam(name = "nam", required = false) Integer nam,
            Model model) {

        // Tổng doanh thu toàn hệ thống
        BigDecimal tongDoanhThu = thuNhapRepo.getTongThuNhap();

        // Doanh thu tháng hiện tại
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDateTime.now();
        BigDecimal doanhThuThang = thuNhapRepo.getTongThuNhapTrongKhoang(startOfMonth, endOfMonth);

        BigDecimal doanhThuQuy = null;
        List<ThuNhapNenTang> chiTietList;

        // Trường hợp lọc theo quý + năm
        if (quy != null && quy >= 1 && quy <= 4 && nam != null) {
            int startMonth = (quy - 1) * 3 + 1;
            int endMonth = startMonth + 2;

            LocalDateTime startQuy = LocalDate.of(nam, startMonth, 1).atStartOfDay();
            LocalDateTime endQuy = YearMonth.of(nam, endMonth).atEndOfMonth().atTime(LocalTime.MAX);

            doanhThuQuy = thuNhapRepo.getTongThuNhapTrongKhoang(startQuy, endQuy);
            chiTietList = thuNhapRepo.findByNgaynhanBetween(startQuy, endQuy);

            model.addAttribute("quy", quy);
            model.addAttribute("nam", nam);

        } else if (start != null && end != null) {
            // Trường hợp lọc theo khoảng ngày
            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

            doanhThuQuy = thuNhapRepo.getTongThuNhapTrongKhoang(startDateTime, endDateTime);
            chiTietList = thuNhapRepo.findByNgaynhanBetween(startDateTime, endDateTime);

            model.addAttribute("start", start);
            model.addAttribute("end", end);

        } else {
            // Không lọc: lấy toàn bộ
            doanhThuQuy = null;
            chiTietList = thuNhapRepo.findAll();
        }

        // Tính doanh thu theo từng quý của năm hiện tại (dùng cho biểu đồ)
        int namHienTai = LocalDate.now().getYear();
        List<BigDecimal> doanhThuTheoQuy = new ArrayList<>();

        for (int quys = 1; quys <= 4; quys++) {
            int startMonth = (quys - 1) * 3 + 1;
            int endMonth = startMonth + 2;

            LocalDateTime startQuy = LocalDate.of(namHienTai, startMonth, 1).atStartOfDay();
            LocalDateTime endQuy = YearMonth.of(namHienTai, endMonth).atEndOfMonth().atTime(LocalTime.MAX);

            BigDecimal doanhThu = thuNhapRepo.getTongThuNhapTrongKhoang(startQuy, endQuy);
            if (doanhThu == null) {
                doanhThu = BigDecimal.ZERO;
            }
            doanhThuTheoQuy.add(doanhThu);
        }

        // Gửi dữ liệu ra view
        model.addAttribute("doanhThuTheoQuy", doanhThuTheoQuy);
        model.addAttribute("nam", namHienTai);

        model.addAttribute("doanhThu", tongDoanhThu);
        model.addAttribute("doanhThuThang", doanhThuThang);
        model.addAttribute("doanhThuQuy", doanhThuQuy);
        model.addAttribute("chiTietDoanhThu", chiTietList);

        return "views/gdienQuanLy/doanhthu";
    }

    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> locDoanhThuApi(
            @RequestParam(name = "quy", required = false) Integer quy,
            @RequestParam(name = "nam", required = false) Integer nam) {

        Map<String, Object> res = new HashMap<>();

        if (quy != null && nam != null) {
            int startMonth = (quy - 1) * 3 + 1;
            int endMonth = startMonth + 2;

            LocalDateTime startQuy = LocalDate.of(nam, startMonth, 1).atStartOfDay();
            LocalDateTime endQuy = YearMonth.of(nam, endMonth).atEndOfMonth().atTime(LocalTime.MAX);

            BigDecimal doanhThuQuy = thuNhapRepo.getTongThuNhapTrongKhoang(startQuy, endQuy);
            List<ThuNhapNenTang> chiTietList = thuNhapRepo.findByNgaynhanBetween(startQuy, endQuy);

            // Format dữ liệu trả về
            res.put("doanhThuQuy", doanhThuQuy != null ? doanhThuQuy.toString() + " VNĐ" : null);

            // HTML render sẵn chi tiết doanh thu (thay thế tbody)
            StringBuilder html = new StringBuilder();
            int index = 1;
            for (ThuNhapNenTang item : chiTietList) {
                html.append("<tr>")
                        .append("<td>").append(index++).append("</td>")
                        .append("<td>").append(item.getTenKhoaHoc()).append("</td>")
                        .append("<td>").append(item.getThuocGiangVien()).append("</td>")
                        .append("<td>").append(item.getSotiennhan().toPlainString()).append(" VNĐ</td>")
                        .append("<td>")
                        .append(item.getNgaynhan().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .append("</td>")
                        .append("</tr>");
            }
            res.put("html", html.toString());
        }

        return res;
    }

}
