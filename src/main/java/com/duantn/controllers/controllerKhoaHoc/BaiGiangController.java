package com.duantn.controllers.controllerKhoaHoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.duantn.entities.BaiGiang;
import com.duantn.services.BaiGiangService;

@Controller
public class BaiGiangController {

    @Autowired
    private BaiGiangService baiGiangService;

    @GetMapping("/baigiang/chitiet/{id}")
    public String chiTietBaiGiang(@PathVariable("id") Integer id, Model model) {
        BaiGiang baiGiang = baiGiangService.findBaiGiangById(id);
        model.addAttribute("baiGiang", baiGiang);
        return "views/KhoaHoc/ChiTietBaiGiang";
    }
}
