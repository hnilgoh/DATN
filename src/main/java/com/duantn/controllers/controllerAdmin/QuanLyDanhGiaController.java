package com.duantn.controllers.controllerAdmin;

import org.springframework.stereotype.Controller;

import com.duantn.entities.BinhLuan;
import com.duantn.entities.DanhGia;
import com.duantn.entities.KhoaHoc;
import com.duantn.repositories.BinhLuanRepository;
import com.duantn.repositories.DanhGiaRepository;
import com.duantn.repositories.KhoaHocRepository;
import com.duantn.services.DanhGiaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quanly-danh-gia-binh-luan")
@RequiredArgsConstructor
public class QuanLyDanhGiaController {

    private final DanhGiaRepository danhGiaRepo;
    private final BinhLuanRepository binhLuanRepo;
    private final KhoaHocRepository khoaHocRepo;
    private final DanhGiaService danhGiaService;

    @GetMapping
    public String showFeedbackPage(
            @RequestParam(value = "courseId", required = false) Integer courseId,
            @RequestParam(value = "rating", required = false) Integer rating,
            Model model) {

        List<KhoaHoc> courses = khoaHocRepo.findAll();
        List<DanhGia> feedbackList = danhGiaRepo.findAll();
        List<BinhLuan> commentList = binhLuanRepo.findAll();

        // Lọc theo khóa học
        if (courseId != null) {
            feedbackList = feedbackList.stream()
                    .filter(f -> f.getKhoahoc().getKhoahocId().equals(courseId))
                    .collect(Collectors.toList());

            commentList = commentList.stream()
                    .filter(c -> c.getBaiGiang() != null
                            && c.getBaiGiang().getChuong() != null
                            && c.getBaiGiang().getChuong().getKhoahoc().getKhoahocId().equals(courseId))
                    .collect(Collectors.toList());
        }

        // Lọc theo số sao
        if (rating != null) {
            feedbackList = feedbackList.stream()
                    .filter(f -> f.getDiemDanhGia().equals(rating))
                    .collect(Collectors.toList());
        }

        // Tính trung bình rating sau khi lọc
        double avgRating = feedbackList.stream()
                .mapToInt(DanhGia::getDiemDanhGia)
                .average()
                .orElse(0);

        // Tính phân bố sao sau khi lọc
        Map<Integer, Long> ratingMap = feedbackList.stream()
                .collect(Collectors.groupingBy(DanhGia::getDiemDanhGia, Collectors.counting()));

        List<Long> chartData = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            chartData.add(ratingMap.getOrDefault(i, 0L));
        }

        model.addAttribute("courses", courses);
        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("commentList", commentList);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("totalReviews", feedbackList.size());
        model.addAttribute("chartData", chartData);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedRating", rating);

        return "views/gdienQuanLy/quanly-danh-gia";
    }

    @GetMapping("/api/danh-gia")
    @ResponseBody
    public Map<String, Object> getFeedbackAjax(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer rating) {

        List<DanhGia> list = danhGiaRepo.findAll().stream()
                .filter(f -> courseId == null || f.getKhoahoc().getKhoahocId().equals(courseId))
                .filter(f -> rating == null || f.getDiemDanhGia().equals(rating))
                .collect(Collectors.toList());

        // Tính lại avg và chart
        double avgRating = list.stream()
                .mapToInt(DanhGia::getDiemDanhGia)
                .average()
                .orElse(0);

        Map<Integer, Long> ratingMap = list.stream()
                .collect(Collectors.groupingBy(DanhGia::getDiemDanhGia, Collectors.counting()));
        List<Long> chartData = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            chartData.add(ratingMap.getOrDefault(i, 0L));
        }

        // Chuẩn bị dữ liệu trả về
        Map<String, Object> result = new HashMap<>();
        result.put("feedbackList", list.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("danhgiaId", f.getDanhgiaId());
            map.put("diemDanhGia", f.getDiemDanhGia());
            map.put("noiDung", f.getNoiDung());
            map.put("ngayDanhGia", f.getNgayDanhGia().toString());
            map.put("taikhoan", Map.of("email", f.getTaikhoan().getEmail()));
            map.put("khoahoc", Map.of(
                    "tenKhoaHoc", f.getKhoahoc().getTenKhoaHoc(),
                    "giangVien",
                    Map.of("taikhoan", Map.of("name", f.getKhoahoc().getGiangVien().getTaikhoan().getName()))));
            return map;
        }).collect(Collectors.toList()));

        result.put("avgRating", avgRating);
        result.put("totalReviews", list.size());
        result.put("chartData", chartData);

        return result;
    }

    @GetMapping("/api/binh-luan")
    @ResponseBody
    public List<Map<String, Object>> getCommentsAjax(
            @RequestParam(value = "courseId", required = false) Integer courseId,
            @RequestParam(value = "sortComment", defaultValue = "desc") String sort) {

        List<BinhLuan> list = binhLuanRepo.findAll().stream()
                .filter(c -> c.getBaiGiang() != null &&
                        c.getBaiGiang().getChuong() != null &&
                        c.getBaiGiang().getChuong().getKhoahoc() != null &&
                        (courseId == null ||
                                c.getBaiGiang().getChuong().getKhoahoc().getKhoahocId().equals(courseId)))
                .sorted((a, b) -> {
                    if ("asc".equals(sort)) {
                        return a.getNgayBinhLuan().compareTo(b.getNgayBinhLuan());
                    } else {
                        return b.getNgayBinhLuan().compareTo(a.getNgayBinhLuan());
                    }
                })
                .collect(Collectors.toList());

        return list.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getBinhluanId());
            map.put("email", c.getTaikhoan().getEmail());
            map.put("content", c.getNoiDung());
            map.put("date", c.getNgayBinhLuan().toString());
            map.put("courseName", c.getBaiGiang().getChuong().getKhoahoc().getTenKhoaHoc());
            return map;
        }).collect(Collectors.toList());
    }

    @DeleteMapping("/api/binh-luan/{id}")
    @ResponseBody
    public String deleteCommentAjax(@PathVariable("id") Integer id) {
        binhLuanRepo.deleteById(id);
        return "deleted";
    }

    @GetMapping(value = "/api/chi-tiet-danh-gia/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFeedbackDetail(@PathVariable("id") Integer id) {
        DanhGia fb = danhGiaService.findById(id).orElse(null);
        if (fb == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("avatar", fb.getKhoahoc().getAnhBia()); // hoặc getAvatar()
        data.put("khoaHoc", fb.getKhoahoc().getTenKhoaHoc());
        data.put("avatarHocVien", fb.getTaikhoan().getAvatar()); // ✅ avatar học viên
        data.put("giangVien", fb.getKhoahoc().getGiangVien().getTaikhoan().getName());
        data.put("hocVien", fb.getTaikhoan().getName());
        data.put("sao", fb.getDiemDanhGia());
        data.put("noiDung", fb.getNoiDung());
        data.put("ngay", fb.getNgayDanhGia());

        return ResponseEntity.ok(data);
    }

}