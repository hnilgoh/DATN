package com.duantn.controllers.controllerChung;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ve-chung-toi")
public class GioiThieuController {
    @RequestMapping()
    public String requestMethodName() {
        return "views/gdienChung/gioithieu";
    }

}
