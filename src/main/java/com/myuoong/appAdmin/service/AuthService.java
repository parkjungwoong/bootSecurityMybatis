package com.myuoong.appAdmin.service;

import com.myuoong.appAdmin.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserMapper mapper;

    public Map checkIdPwUser(Map user){
        String id = (String)user.get("id");

        Map checkUser = mapper.findById(id);
        String oriPw = (String)checkUser.get("pw");
        String pw = (String)user.get("pw");

        //if(!oriPw.equals(pw))
        return checkUser;
    }
}
