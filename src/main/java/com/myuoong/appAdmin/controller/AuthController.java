package com.myuoong.appAdmin.controller;

import com.myuoong.appAdmin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthController {

    @Autowired
    private AuthService service;

    /**
     * 1차 인증
     * @param user
     * @return
     */
    @RequestMapping("/idPwLogin")
    public Map idPwLogin(Map user){
        return service.checkIdPwUser(user);
    }

    //2차 인증 마지막 확인은 시큐어리티 필터에서 함
}
