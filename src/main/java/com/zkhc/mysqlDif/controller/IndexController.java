package com.zkhc.mysqlDif.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IndexController {
    @RequestMapping("/")
    public Object getDifInfo(Model model){
        return "index.html";
    }
}
