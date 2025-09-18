package com.duantn.controllers.controllerHocVien;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class KetQuaChiTietController {

    @RequestMapping("/ket-qua-lam-bai-chi-tiet")
    public String requestMethodName() {
        return "views/gdienHocVien/ket-qua-chi-tiet";
    }
}
