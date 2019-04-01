package com.myuoong.appAdmin.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import static com.myuoong.appAdmin.common.ComConst.HEADER_PREFIX;

@Slf4j
public class CommonUtils {

    public static String extract(String header){
        if (StringUtils.isBlank(header)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }

        if (header.length() < HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid authorization header size.");
        }

        return header.substring(HEADER_PREFIX.length(), header.length());
    }

    //todo: 공통 mac 유틸로 교체하기
    public static String HmacMake(String data, String key) throws Exception{
        String alg = "HmacSHA256";
        Mac hmac = Mac.getInstance(alg);
        hmac.init(new SecretKeySpec(key.getBytes(),alg));
        hmac.update(data.getBytes());
        byte[] hash = hmac.doFinal();
        return DatatypeConverter.printHexBinary(hash).toUpperCase();
    }


}
