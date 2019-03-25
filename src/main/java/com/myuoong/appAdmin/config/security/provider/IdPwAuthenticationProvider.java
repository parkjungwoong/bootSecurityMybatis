package com.myuoong.appAdmin.config.security.provider;

import com.myuoong.appAdmin.model.UserContext;
import com.myuoong.appAdmin.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * 1차 인증에서 사용하는 인증 프로바이더
 */
@Slf4j
public class IdPwAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthService authService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        //todo: 여기서 인증 검사 로직

        username = "admin";//test code
        List<GrantedAuthority> authorities = new ArrayList<>();//test code
        authorities.add(new SimpleGrantedAuthority("ADMIN"));//test code

        UserContext userContext = UserContext.create(username, authorities);

        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass));
    }
}
