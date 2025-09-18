package com.duantn.controllers.controllerGiangVien;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class QuanLyBinhLuanController {
    @RequestMapping("/giangvien/binh-luan-tu-bai-giang")
    public String requestMethodName() {
        return "views/gdienGiangVien/quan-ly-binh-luan";
    }

}