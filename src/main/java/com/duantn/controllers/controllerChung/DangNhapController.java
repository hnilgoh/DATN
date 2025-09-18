package com.duantn.controllers.controllerChung;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class DangNhapController {

    @GetMapping("/dangnhap")
    public String loginPage() {
        return "views/gdienChung/login";
    }

}
