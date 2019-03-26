package com.myuoong.appAdmin.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myuoong.appAdmin.common.util.CommonUtils;
import com.myuoong.appAdmin.config.security.jwt.token.JwtAuthenticationToken;
import com.myuoong.appAdmin.config.security.jwt.token.RawAccessJwtToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.myuoong.appAdmin.common.ComConst.AUTHENTICATION_HEADER_NAME;

/**
 * 2차 인증에서 사용하는 필터
 */
@Slf4j
public class TalkAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public TalkAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        //요청 형식 체크
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.debug("Authentication method not supported. Request method: " + request.getMethod());
            throw new AuthenticationServiceException("use post");
        }

        //토큰 있는지 검사
        String tokenPayload = request.getHeader(AUTHENTICATION_HEADER_NAME);
        if(tokenPayload == null) throw new AuthenticationServiceException("ID, PW 필수 파라미터 누락");

        RawAccessJwtToken token = new RawAccessJwtToken(CommonUtils.extract(tokenPayload));
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("TalkAuthenticationFilter 통과 : {}",authResult.getName());
        //todo: 2차 인증까지 완료 후 api요청에 접근할 수 있는 토큰발급
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("TalkAuthenticationFilter 실패 : {}",failed.getCause());
        SecurityContextHolder.clearContext();
        response.setStatus(401);
    }
}
