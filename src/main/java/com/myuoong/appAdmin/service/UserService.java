package com.myuoong.appAdmin.service;

import com.myuoong.appAdmin.config.security.jwt.token.JwtTokenFactory;
import com.myuoong.appAdmin.mapper.UserMapper;
import com.myuoong.appAdmin.model.AuthTrade;
import com.myuoong.appAdmin.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper mapper;

    @Autowired
    private JwtTokenFactory tokenFactory;

    public User selectUserById(String id){
        return mapper.selectUserById(id);
    }

    public AuthTrade selectAuthTradeById(String tradeId) {
        return mapper.selectAuthTradeById(tradeId);
    }

    public void insertAuthTrade(AuthTrade authTrade) {
        mapper.insertAuthTrade(authTrade);
    }

}
