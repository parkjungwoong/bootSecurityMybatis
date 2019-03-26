package com.myuoong.appAdmin.service;

import com.myuoong.appAdmin.mapper.UserMapper;
import com.myuoong.appAdmin.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper mapper;

    public User selectUserById(String id){
        return mapper.selectUserById(id);
    }

    public boolean selectTalkAuthTradeByTradeId(String tradeId) {
        //todo: 인증 관련 DB
        return true;
    }
}
