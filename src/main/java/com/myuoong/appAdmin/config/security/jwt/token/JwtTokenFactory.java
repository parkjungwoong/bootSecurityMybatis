package com.myuoong.appAdmin.config.security.jwt.token;

import com.myuoong.appAdmin.common.util.CommonUtils;
import com.myuoong.appAdmin.common.util.DateUtil;
import com.myuoong.appAdmin.config.security.jwt.JwtSettings;
import com.myuoong.appAdmin.model.Scopes;
import com.myuoong.appAdmin.model.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.myuoong.appAdmin.common.ComConst.*;


@Component
@Slf4j
public class JwtTokenFactory {

    @Autowired
    private JwtSettings settings;

    /**
     * 엑세스 토큰 생성
     * @param userContext 유저 정보
     * @return 엑세스 토큰
     */
    public AccessJwtToken createAccessJwtToken(UserContext userContext) {
        if( StringUtils.isBlank(userContext.getUserId()) ) throw new IllegalArgumentException("user id은 필수값");
        if( userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) throw new IllegalArgumentException("권한은 필수값");
        if( userContext.getUser() == null || StringUtils.isBlank(userContext.getUser().getSvcId()) ) throw new IllegalArgumentException("user SvcId은 필수값");
        if( StringUtils.isBlank(userContext.getUser().getPhone()) ) throw new IllegalArgumentException("user phone은 필수값");

        Claims claims = Jwts.claims().setSubject(userContext.getUserId());
        claims.put(JWT_KEY_SCOPES, userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
        claims.put(JWT_KEY_SVC_ID, userContext.getUser().getSvcId());
        claims.put(JWT_KEY_PHONE, userContext.getUser().getPhone());

        LocalDateTime currentTime = LocalDateTime.now();
        
        String token = Jwts.builder()
          .setClaims(claims)
          .setIssuer(settings.getTokenIssuer())
          .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
          .setExpiration(Date.from(currentTime
              .plusMinutes(settings.getTokenExpirationTime())
              .atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
        .compact();

        return new AccessJwtToken(token, claims);
    }

    /**
     * 리프레시 토큰 생성
     * @param userContext 유저 정보
     * @return 리프레시 토큰
     */
    public JwtToken createRefreshToken(UserContext userContext) {
        if( StringUtils.isBlank(userContext.getUserId()) ) throw new IllegalArgumentException("user id은 필수값");

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject(userContext.getUserId());
        claims.put(JWT_KEY_SCOPES, Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

        String token = Jwts.builder()
          .setClaims(claims)
          .setIssuer(settings.getTokenIssuer())
          .setId(UUID.randomUUID().toString())
          .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
          .setExpiration(Date.from(currentTime
              .plusMinutes(settings.getRefreshTokenExpTime())
              .atZone(ZoneId.systemDefault()).toInstant()))
          .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
        .compact();

        return new AccessJwtToken(token, claims);
    }


    /**
     * 톡인증용 토큰 생성
     * @param userContext 유저 정보
     * @return 톡인증 요청용 토큰
     */
    public TalkToken createTalkAuthToken(UserContext userContext){
        if( StringUtils.isBlank(userContext.getUserId()) ) throw new IllegalArgumentException("user id은 필수값");
        if( StringUtils.isBlank(userContext.getUser().getPhone()) ) throw new IllegalArgumentException("user phone은 필수값");

        //톡인증 거래번호 생성
        final String talkTradeNo = DateUtil.getCurrent()+userContext.getUserId();

        log.info("요청자 : {}, 톡인증 거래번호 : {}",userContext.getUserId(), talkTradeNo);

        //todo: 요청 url 생성 부분 리펙토링
        String mac = "";
        try {
            mac = CommonUtils.HmacMake(
                    TALK_AUTH_SVC_ID
                            +TALK_AUTH_SITE_NM
                            +userContext.getUser().getPhone()
                            +talkTradeNo
                            +TALK_AUTH_RETURN_URL
                    ,TALK_AUTH_MAC_KEY);
        } catch (Exception e){
            log.error("mac err",e);
        }

        //톡인증 요청 url생성
        final String tauthUrl = TALK_AUTH_URL
                +"?svcId="+TALK_AUTH_SVC_ID
                +"&siteNm="+TALK_AUTH_SITE_NM
                +"&phoneNo="+userContext.getUser().getPhone()
                +"&tNo="+talkTradeNo
                +"&returnUrl="+TALK_AUTH_RETURN_URL
                +"&mac="+mac;

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject(userContext.getUserId());

        claims.put(JWT_KEY_SCOPES, Arrays.asList(Scopes.TALK_TOKEN.authority()));
        claims.put(JWT_KEY_TALK_TRADE_NO, talkTradeNo);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusMinutes(settings.getTalkTokenExpirationTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();

        return new TalkToken(token, tauthUrl);
    }

}
