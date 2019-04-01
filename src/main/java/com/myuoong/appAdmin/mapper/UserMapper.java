package com.myuoong.appAdmin.mapper;

import com.myuoong.appAdmin.model.AuthTrade;
import com.myuoong.appAdmin.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface UserMapper {
    User selectUserById(String id);
    AuthTrade selectAuthTradeById(String tradeId);
    void insertAuthTrade(AuthTrade authTrade);
}
