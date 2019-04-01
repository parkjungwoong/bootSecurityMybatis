package com.myuoong.appAdmin.config.security.provider;

import com.myuoong.appAdmin.model.User;
import com.myuoong.appAdmin.model.UserContext;
import com.myuoong.appAdmin.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 1차 인증에서 사용하는 인증 프로바이더
 */
@Slf4j
@Component
public class IdPwAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("IdPwAuthenticationProvider start");

        String id = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        User user = userService.selectUserById(id);

        if(user == null){
            log.info("미등록 ID: {}", id);
            throw new UsernameNotFoundException("미등록 ID: "+id);
        }

        if(!password.equals(user.getPw())){
            log.info("비밀번호 미일치 {}", password);
            throw new BadCredentialsException("비밀번호 미일치");
        }

        if(user.getRole() == null){
            log.info("계정 권한 없음");
            throw new InsufficientAuthenticationException("계정 권한 없음");
        }

        UserContext userContext = UserContext.create(user, null);

        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass));
    }
}
