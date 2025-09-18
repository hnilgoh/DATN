package com.duantn.controllers.controllerKhoaHoc;

import com.duantn.entities.Chuong;
import com.duantn.repositories.ChuongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class ChuongController {
    @Autowired
    private ChuongRepository chuongRepository;

    @GetMapping("/chuong")
    public String hienThiDanhSachChuong(Model model) {
        List<Chuong> danhSach = chuongRepository.findAll();
        model.addAttribute("chuongs", danhSach);
        return "views/KhoaHoc/danhSachChuong";
    }

    @GetMapping("/chuong/{id}")
    public String chiTietChuong(@PathVariable("id") Integer id, Model model) {
        Chuong chuong = chuongRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Chuong Id: " + id));
        model.addAttribute("chuong", chuong);
        return "views/KhoaHoc/xemChiTietChuong";
    }

    @GetMapping("/chuong/danhsach/{khoahocId}")
    public String hienThiDanhSachChuongTheoKhoaHoc(@PathVariable("khoahocId") Integer khoahocId, Model model) {
        List<Chuong> danhSach = chuongRepository.findAll().stream()
            .filter(c -> c.getKhoahoc().getKhoahocId().equals(khoahocId))
            .toList();
        model.addAttribute("chuongs", danhSach);
        model.addAttribute("khoahocId", khoahocId);
        return "views/KhoaHoc/DanhSachChuong";
    }
}