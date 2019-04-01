package com.myuoong.appAdmin.config.security.filter;

import com.myuoong.appAdmin.common.util.CommonUtils;
import com.myuoong.appAdmin.config.security.jwt.token.JwtAuthenticationToken;
import com.myuoong.appAdmin.config.security.jwt.token.RawAccessJwtToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.myuoong.appAdmin.common.ComConst.AUTHENTICATION_HEADER_NAME;

/**
 * api 요청 시 jwt 토큰 검사 필터
 */
@Slf4j
public class ResourceAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public ResourceAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("ResourceAuthenticationFilter start");

        //요청 형식 체크
        if(!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.info("잘못된 요청 형식 : " + request.getMethod());
            throw new AuthenticationServiceException("잘못된 요청 형식");
        }

        //토큰 있는지 검사
        String tokenPayload = request.getHeader(AUTHENTICATION_HEADER_NAME);

        if(tokenPayload == null){
            log.info("JWT 토큰 누락");
            throw new AuthenticationServiceException("JWT 토큰 누락");
        }

        RawAccessJwtToken token = new RawAccessJwtToken(CommonUtils.extract(tokenPayload));

        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("ResourceAuthenticationFilter success");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        response.setStatus(401);
    }
}
