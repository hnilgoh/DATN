package com.duantn.controllers.controllerChung;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HuongDanSuDungController {
    @GetMapping("/&huong-&dan-&su-&dung")
    public String getMethodName() {
        return "views/gdienChung/huongdansudung";
    }

}
