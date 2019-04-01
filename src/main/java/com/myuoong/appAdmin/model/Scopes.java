package com.myuoong.appAdmin.model;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Arrays;

import static com.myuoong.appAdmin.common.ComConst.JWT_KEY_TALK_TRADE_NO;
import static com.myuoong.appAdmin.common.ComConst.TALK_AUTH_SUCCESS;

@Slf4j
public enum Scopes {

    //가맹점
    ADMIN {
        @Override
        public void check(Jws<Claims> claimsJws, Authentication authentication) throws AuthenticationException {

        }
    }

    //자동 로그인
    ,AUTO_LOGIN {
        @Override
        public void check(Jws<Claims> claimsJws, Authentication authentication) throws AuthenticationException {

        }
    }

    //리프레시 토큰
    ,REFRESH_TOKEN {
        @Override
        public void check(Jws<Claims> claimsJws, Authentication authentication) throws AuthenticationException{

        }
    }

    //톡인증
    ,TALK_TOKEN {
        @Override
        public void check(Jws<Claims> claimsJws, Authentication authentication) throws AuthenticationException {
            UserContext userContext = (UserContext) authentication.getPrincipal();

            String tradeIdInReq = userContext.getTalkAuthReturn().getTNo();//요청시 전달된 톡인증 거래번호
            String tradeIdInJwt = claimsJws.getBody().get(JWT_KEY_TALK_TRADE_NO, String.class);//1차 인증때 전달된 톡인증 거래번호

            //톡인증 성공 여부 체크
            if( !TALK_AUTH_SUCCESS.equals(userContext.getTalkAuthReturn().getResultCd())){
                log.info("톡인증 실패");
                throw new AuthenticationServiceException("톡인증 실패");
            }

            //토큰 거래번호와 요청 거래 번호 검증
            if(!tradeIdInReq.equals(tradeIdInJwt)){
                log.info("인증 거래번호 불일치 원 거래번호 {}, 요청 거래번호 {}",tradeIdInReq, tradeIdInJwt);
                throw new BadCredentialsException("인증 거래번호 불일치");
            }
            //todo: mac 검증

        }
    };
    
    public String authority() {
        return "ROLE_" + this.name();
    }

    public static Scopes getScope(String scopeNm){
        return Arrays.stream(Scopes.values()).filter(scopes ->
            scopes.authority().equals(scopeNm)
        ).findAny().orElseThrow(() -> new IllegalArgumentException("일치하는 권한명 없음 : "+scopeNm));
    }

    /**
     * 권한별 검증 로직
     * @param claimsJws jwt 토큰
     * @param authentication 인증 정보
     * @throws AuthenticationException 인증관련 에러
     */
    public abstract void check(Jws<Claims> claimsJws, Authentication authentication) throws AuthenticationException;
}
