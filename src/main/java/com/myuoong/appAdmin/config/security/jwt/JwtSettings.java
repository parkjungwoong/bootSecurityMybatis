package com.myuoong.appAdmin.config.security.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtSettings {
    //엑세스 토큰 만료 기간
    @Value("${jwt.tokenExpirationTime}")
    private Integer tokenExpirationTime;

    //
    @Value("${jwt.tokenIssuer}")
    private String tokenIssuer;
    
    //토큰 서명 키
    @Value("${jwt.tokenSigningKey}")
    private String tokenSigningKey;

    //리프레시 토큰 만료 기간
    @Value("${jwt.refreshTokenExpTime}")
    private Integer refreshTokenExpTime;

    //톡인증 토큰 만료 기간
    @Value("${jwt.talkTokenExpirationTime}")
    private Integer talkTokenExpirationTime;
}
