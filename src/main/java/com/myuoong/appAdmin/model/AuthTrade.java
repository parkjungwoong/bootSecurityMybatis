package com.myuoong.appAdmin.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("authTrade")
public class AuthTrade {
    private String tradeNo;
    private String userId;
    private String regDt;
    private String autoYn;
}

