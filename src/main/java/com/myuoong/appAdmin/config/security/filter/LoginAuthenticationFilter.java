package com.myuoong.appAdmin.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myuoong.appAdmin.model.LoginReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 1차 인증요청에서 동작하는 필터
 * HTTP 요청 메소드 ( POST ) 검사
 * 사용자 입력값 검사
 *
 * 인증 성공, 실패시 핸들러 처리
 */
@Slf4j
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public LoginAuthenticationFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper) {
        super(defaultFilterProcessesUrl);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        logger.debug("::: LoginAuthenticationFilter start");

        //요청 형식 체크
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.debug("Authentication method not supported. Request method: " + request.getMethod());
            throw new AuthenticationServiceException("use post");
        }

        //인증 파라미터 체크
        LoginReq loginReq = objectMapper.readValue(request.getReader(), LoginReq.class);

        if(StringUtils.isBlank(loginReq.getId()) || StringUtils.isBlank(loginReq.getPw())) throw new AuthenticationServiceException("ID, PW 필수 파라미터 누락");

        /*
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));*/

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginReq.getId(), loginReq.getPw(), null);

        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("LoginAuthenticationFilter 통과 : {}",authResult.getName());
        //todo: tauth 인증 요청을 할 수 있는 토큰 발급
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("LoginAuthenticationFilter 실패 : {}",failed.getCause());
        SecurityContextHolder.clearContext();
        response.setStatus(401);
    }
}
