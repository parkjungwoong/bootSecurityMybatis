package com.myuoong.appAdmin.config.security.provider;

import com.myuoong.appAdmin.config.security.jwt.token.RawAccessJwtToken;
import com.myuoong.appAdmin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


/**
 * 2차 인증에서 사용하는 인증프로바이더
 */
@Slf4j
@Component
public class TAuthFinAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Tauth provider work");
        String tradeId = (String) authentication.getPrincipal();
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

        //인증 검사
        boolean isTalkAuth = userService.selectTalkAuthTradeByTradeId(tradeId);

        if(!isTalkAuth) throw new BadCredentialsException("인증 결과 없음");

        //todo : 컨트롤러까지 넘겨서 처리 할 건지 아니면 여기서 정보 조회후 넘겨줘서 성공 핸들러에서 jwt 토큰 만들어서 줄건지?

        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
