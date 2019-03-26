package com.myuoong.appAdmin.mapper;

import com.myuoong.appAdmin.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface UserMapper {
    User selectUserById(String id);
}
