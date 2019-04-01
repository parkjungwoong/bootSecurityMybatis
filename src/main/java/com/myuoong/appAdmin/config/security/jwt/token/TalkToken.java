package com.myuoong.appAdmin.config.security.jwt.token;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 톡인증 토큰
 */
@AllArgsConstructor
@Data
public class TalkToken implements JwtToken {

    private final String token;
    private final String talkAuthURL;

    @Override
    public String getToken() {
        return this.token;
    }
}
