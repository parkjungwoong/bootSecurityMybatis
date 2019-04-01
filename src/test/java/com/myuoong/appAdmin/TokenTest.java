package com.myuoong.appAdmin;

import com.myuoong.appAdmin.config.security.jwt.JwtSettings;
import com.myuoong.appAdmin.config.security.jwt.token.JwtTokenFactory;
import com.myuoong.appAdmin.config.security.jwt.token.TalkToken;
import com.myuoong.appAdmin.model.Scopes;
import com.myuoong.appAdmin.model.User;
import com.myuoong.appAdmin.model.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.myuoong.appAdmin.common.ComConst.JWT_KEY_SCOPES;
import static com.myuoong.appAdmin.common.ComConst.RULE_SPLIT_STR;
import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TokenTest {

    @Autowired
    JwtTokenFactory jwtTokenFactory;

    @Autowired
    JwtSettings jwtSettings;

    @Test
    public void 톡인증_요청용_토큰생성(){

        User user = new User();
        user.setId("admin");
        user.setPhone("01084226318");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        UserContext userContext = UserContext.create(user, authorities);

        TalkToken token = jwtTokenFactory.createTalkAuthToken(userContext);

        assertNotNull(token.getToken());
        assertNotNull(token.getTalkAuthURL());

        log.info("톡인증 요청용 토큰 : {}", token.getToken());
        log.info("톡인증 요청 url : {}", token.getTalkAuthURL());
    }

    @Test(expected=IllegalArgumentException.class)
    public void 톡인증_토큰_아이디_파라미터_필수_확인(){
        User user = new User();
        user.setPhone("01084226318");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        UserContext userContext = UserContext.create(user, authorities);

        jwtTokenFactory.createTalkAuthToken(userContext);
    }

    @Test(expected=IllegalArgumentException.class)
    public void 톡인증_토큰_전화번호_파라미터_필수_확인(){
        User user = new User();
        user.setId("1234");
        user.setPhone("");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        UserContext userContext = UserContext.create(user, authorities);

        jwtTokenFactory.createTalkAuthToken(userContext);
    }

    @Test
    public void 토큰_파싱(){
        User user = new User();
        user.setId("admin");
        user.setPhone("01084226318");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        UserContext userContext = UserContext.create(user, authorities);

        TalkToken token = jwtTokenFactory.createTalkAuthToken(userContext);

        log.info("token = {}", token.getToken());

        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(jwtSettings.getTokenSigningKey()).parseClaimsJws(token.getToken());

        assertEquals(user.getId(), jwsClaims.getBody().getSubject());
        assertEquals(1, jwsClaims.getBody().get(JWT_KEY_SCOPES,List.class).size());
        assertEquals(Scopes.TALK_TOKEN.authority(), jwsClaims.getBody().get(JWT_KEY_SCOPES,List.class).get(0));
        assertNotNull(jwsClaims.getBody().get("talkTradeNo"));

        log.info("signature = {}",jwsClaims.getSignature());
        log.info("header = {}",jwsClaims.getHeader());
        log.info("body = {}",jwsClaims.getBody());
    }

    @Test
    public void validationCheckToken(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        User user = new User();
        user.setId("admin");
        user.setPhone("01084226318");

        UserContext userContext = UserContext.create(user, authorities);

        TalkToken token = jwtTokenFactory.createTalkAuthToken(userContext);

        log.info("token = {}", token.getToken());
        log.info("url = {}", token.getTalkAuthURL());

        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(jwtSettings.getTokenSigningKey()).parseClaimsJws(token.getToken());

        log.info("signature = {}",jwsClaims.getSignature());
        log.info("header = {}",jwsClaims.getHeader());
        log.info("body = {}",jwsClaims.getBody());
    }

    @Test
    public void scopeEnumTest(){
        String scopeNm = "ROLE_RULE_ADMIN";

        Scopes scopes = Scopes.getScope(scopeNm);

        assertEquals(scopeNm,scopes.authority());
    }

    @Test(expected = IllegalArgumentException.class)
    public void scopeEnumTest_Exception() {
        String scopeNm = "UN_KNOW_SCOPE";

        Scopes.getScope(scopeNm);
    }

    @Test
    public void scopeContainCheck(){
        List<String> scope = new ArrayList<>();

        scope.add(Scopes.TALK_TOKEN.authority());
        scope.add(Scopes.REFRESH_TOKEN.authority());

        assertTrue( scope.contains(Scopes.TALK_TOKEN.authority()) );

        assertTrue( scope.contains(Scopes.REFRESH_TOKEN.authority()) );

        assertFalse( scope.contains("UN_KNOW_SCOPE") );
    }

    @Test
    public void test(){
        User user = new User();
        user.setRole("ADMIN^USER");

        String[] userScopes = user.getRole().split("\\^");

        log.info(""+userScopes.length);

        for(String e : userScopes){
            log.info(e);
        }

        List<GrantedAuthority> authorities = Arrays.stream(userScopes)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities.forEach(authoritie -> log.info(authoritie.getAuthority()));

        String[] tt = (user.getRole()+RULE_SPLIT_STR+Scopes.AUTO_LOGIN.authority()).split(RULE_SPLIT_STR);

        for(String e : tt){
            log.info(e);
        }


    }
}
