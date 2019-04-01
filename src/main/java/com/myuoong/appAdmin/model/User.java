package com.myuoong.appAdmin.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("user")
public class User {
    private String id;
    private String pw;
    private String phone;
    private String name;
    private String role;
    private String svcId;
    private String useYn;
}
