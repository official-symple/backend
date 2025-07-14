package com.DreamOfDuck.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminController {
    @GetMapping("/admin")
    public String login(){
        return "login/index";
    }
    @GetMapping("/admin/main")
    public String mainPage(){
        return "main/index";
    }
    @GetMapping("/admin/report")
    public String report(){
        return "report/index";
    }
}
