package com.myuoong.appAdmin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/home")
@RestController
public class HomeController {
    @RequestMapping("/test")
    public Map test(){
        Map res = new HashMap();
        res.put("test","home");

        return res;
    }
}
