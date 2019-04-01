package com.myuoong.appAdmin.config.security.provider;

import com.myuoong.appAdmin.config.security.jwt.JwtSettings;
import com.myuoong.appAdmin.config.security.jwt.token.JwtAuthenticationToken;
import com.myuoong.appAdmin.config.security.jwt.token.RawAccessJwtToken;
import com.myuoong.appAdmin.model.Scopes;
import com.myuoong.appAdmin.model.User;
import com.myuoong.appAdmin.model.UserContext;
import com.myuoong.appAdmin.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
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
 * JWT 토큰을 통한 요청에서 사용하는 인증프로바이더
 */
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtSettings settings;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("JwtAuthentication provider work");

        final RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

        //토큰 유효성 검사 및 토큰 복호화
        final Jws<Claims> claimsJws = rawAccessToken.parseClaims(settings.getTokenSigningKey());

        //권한 가져오기
        final List<String> scopes = claimsJws.getBody().get(JWT_KEY_SCOPES, List.class);

        User user = new User();
        user.setId(claimsJws.getBody().getSubject());
        user.setSvcId(claimsJws.getBody().get(JWT_KEY_SVC_ID,String.class));
        user.setPhone(claimsJws.getBody().get(JWT_KEY_PHONE,String.class));

        List<GrantedAuthority> authorities = scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserContext context = UserContext.create(user, authorities);

        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (JwtAuthenticationToken.class.isAssignableFrom(aClass));
    }


}
