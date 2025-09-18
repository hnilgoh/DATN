package com.duantn.controllers.controllerHocVien;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LamTracNghiemController {
    @RequestMapping("/lam-trac-nghiem")
    public String requestMethodName() {
        return "views/gdienHocVien/lam-trac-nghiem";
    }

}
