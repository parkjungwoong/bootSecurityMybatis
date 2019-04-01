package com.myuoong.appAdmin;

import com.myuoong.appAdmin.config.security.jwt.JwtSettings;
import com.myuoong.appAdmin.config.security.jwt.token.JwtTokenFactory;
import com.myuoong.appAdmin.model.Scopes;
import com.myuoong.appAdmin.model.TalkAuthReturn;
import com.myuoong.appAdmin.model.User;
import com.myuoong.appAdmin.model.UserContext;
import com.myuoong.appAdmin.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.myuoong.appAdmin.common.ComConst.*;
import static com.myuoong.appAdmin.common.ComConst.JWT_KEY_SCOPES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@Slf4j
public class UserSeviceTest {

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenFactory jwtTokenFactory;

    @Autowired
    JwtSettings jwtSettings;

    @Test
    public void talkAuthFin(){
        /*final String TEST_TNO = "T123456789";

        User user = new User();
        user.setId("admin");
        user.setPhone("01084226318");
        user.setSvcId("setSvcId");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Scopes.ADMIN.authority()));

        UserContext userContext = UserContext.create(user, authorities);

        TalkAuthReturn talkAuthReturn = TalkAuthReturn.builder()
                .userId(user.getId())
                .tNo(TEST_TNO)
                .build();

        Map result = userService.talkAuthFin(talkAuthReturn,userContext);

        //엑세스 토큰 검증
        String accessToken = (String)result.get(JWT_KEY_ACCESS_TOKEN);
        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(jwtSettings.getTokenSigningKey()).parseClaimsJws(accessToken);

        assertEquals(user.getId(), jwsClaims.getBody().getSubject());
        assertEquals(1, jwsClaims.getBody().get(JWT_KEY_SCOPES,List.class).size());
        assertEquals(Scopes.ADMIN.authority(), jwsClaims.getBody().get(JWT_KEY_SCOPES,List.class).get(0));

        //리프레시 토큰 검증
        String refreshToken = (String)result.get(JWT_KEY_REFRESH_TOKEN);
        jwsClaims = Jwts.parser().setSigningKey(jwtSettings.getTokenSigningKey()).parseClaimsJws(refreshToken);

        assertEquals(user.getId(), jwsClaims.getBody().getSubject());
        assertEquals(TEST_TNO, jwsClaims.getBody().getId());
        assertEquals(1, jwsClaims.getBody().get(JWT_KEY_SCOPES,List.class).size());
        assertEquals(Scopes.REFRESH_TOKEN.authority(), jwsClaims.getBody().get(JWT_KEY_SCOPES,List.class).get(0));*/

    }
}
