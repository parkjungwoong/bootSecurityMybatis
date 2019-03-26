package com.myuoong.appAdmin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@AllArgsConstructor
@Data
@Alias("user")
public class User {
    private String id;
    private String pw;
    private String token;
    private String name;
    private String isExp;
    private String isLock;
    private String isEnable;
    private String role;
}
