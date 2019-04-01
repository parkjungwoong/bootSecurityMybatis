package com.myuoong.appAdmin.config.security.provider;

import com.myuoong.appAdmin.common.util.DateUtil;
import com.myuoong.appAdmin.config.security.jwt.JwtSettings;
import com.myuoong.appAdmin.config.security.jwt.token.JwtAuthenticationToken;
import com.myuoong.appAdmin.config.security.jwt.token.RawAccessJwtToken;
import com.myuoong.appAdmin.config.security.jwt.token.TalkAuthenticationToken;
import com.myuoong.appAdmin.model.*;
import com.myuoong.appAdmin.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.myuoong.appAdmin.common.ComConst.*;

/**
 * 톡인증 토큰을 통한 요청에서 사용하는 인증프로바이더
 */
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class TalkAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtSettings settings;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("TalkAuthenticationProvider provider work");

        final RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

        //토큰 유효성 검사 및 토큰 복호화
        final Jws<Claims> claimsJws = rawAccessToken.parseClaims(settings.getTokenSigningKey());

        //톡인증 검증
        TalkAuthReturn talkAuthReturn = (TalkAuthReturn) authentication.getPrincipal();

        //톡인증 성공 여부 체크
        if( !TALK_AUTH_SUCCESS.equals(talkAuthReturn.getResultCd())){
            log.info("톡인증 실패");
            throw new AuthenticationServiceException("톡인증 실패");
        }

        final String tradeIdInReq = talkAuthReturn.getTNo();//톡인증 서버로부터 받은 거래번호
        final String tradeIdInJwt = claimsJws.getBody().get(JWT_KEY_TALK_TRADE_NO, String.class);//1차 인증때 전달된 톡인증 거래번호

        //톡인증 거래번호 검증
        if(!tradeIdInReq.equals(tradeIdInJwt)){
            log.info("인증 거래번호 불일치 원 거래번호 {}, 요청 거래번호 {}",tradeIdInReq, tradeIdInJwt);
            throw new BadCredentialsException("인증 거래번호 불일치");
        }

        //todo: mac 검증
        /*if(mac 검증 결과값){
            log.info("mac 불일치",tradeIdInReq, tradeIdInJwt);
            throw new BadCredentialsException("mac 불일치");
        }*/

        User user = userService.selectUserById(claimsJws.getBody().getSubject());

        //유저 체크
        if(user == null){
            log.info("미등록 ID: {}", claimsJws.getBody().getSubject());
            throw new UsernameNotFoundException("미등록 ID: "+claimsJws.getBody().getSubject());
        }

        //이중 인증 체크 : 톡인증 거래번호롤 인증 성공 내역 있으면 이미 인증된 건으로 실패 처리
        AuthTrade authTrade = userService.selectAuthTradeById(tradeIdInJwt);

        if(authTrade != null){
            log.info("이미 인증이 완료된 요청 tradeId : {}",tradeIdInJwt);
            throw new BadCredentialsException("이미 인증이 완료된 요청");
        }

        //유저 정보 세팅
        String[] userScopes = user.getRole().split(RULE_SPLIT_STR);;

        List<GrantedAuthority> authorities = Arrays.stream(userScopes)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //자동 로그인일 경우 분기 처리
        if(talkAuthReturn.getAutoYn() != null && talkAuthReturn.getAutoYn().equals("Y")){
            //자동 로그인 권한 추가
            authorities.add(new SimpleGrantedAuthority(Scopes.AUTO_LOGIN.authority()));
        }

        UserContext context = UserContext.create(user, authorities);

        authTrade = new AuthTrade();
        authTrade.setTradeNo(tradeIdInJwt);
        authTrade.setUserId(user.getId());
        authTrade.setRegDt(DateUtil.getCurrent());
        authTrade.setAutoYn(talkAuthReturn.getAutoYn());

        userService.insertAuthTrade(authTrade);

        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (TalkAuthenticationToken.class.isAssignableFrom(aClass));
    }


}
