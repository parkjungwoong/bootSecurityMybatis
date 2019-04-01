package com.myuoong.appAdmin.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myuoong.appAdmin.config.security.jwt.token.JwtTokenFactory;
import com.myuoong.appAdmin.config.security.jwt.token.TalkToken;
import com.myuoong.appAdmin.model.LoginReq;
import com.myuoong.appAdmin.model.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 1차 인증요청에서 동작하는 필터
 * HTTP 요청 메소드 ( POST ) 검사
 * 사용자 입력값 검사
 *
 * 1차 인증 성공시
 *  톡인증 요청용 토큰 발급, 해당 토큰에는 톡인증 거래번호 있음
 */
@Slf4j
public class IdPwAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JwtTokenFactory tokenFactory;

    public IdPwAuthenticationFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper, JwtTokenFactory jwtTokenFactory) {
        super(defaultFilterProcessesUrl);
        this.objectMapper = objectMapper;
        this.tokenFactory = jwtTokenFactory;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        log.info("::: IdPwAuthenticationFilter start");

        //요청 형식 체크
        if(!HttpMethod.POST.name().equals(request.getMethod())) {
            log.info("Authentication method not supported. Request method: " + request.getMethod());
            throw new AuthenticationServiceException("use post");
        }

        //인증 파라미터 체크
        LoginReq loginReq = objectMapper.readValue(request.getReader(), LoginReq.class);

        if(StringUtils.isBlank(loginReq.getId()) || StringUtils.isBlank(loginReq.getPw())) {
            log.info("ID, PW 필수 파라미터 누락");
            throw new AuthenticationServiceException("ID, PW 필수 파라미터 누락");
        }

        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(loginReq.getId(), loginReq.getPw()));
    }

    /**
     * 1차 인증 성공시 핸들러, 응답값 세팅 후 클라이언트에 응답.
     * 응답값
     * - 2차 인증 요청용 토큰 ( 2차인증 요청에만 사용 가능, 나머지 요청에 사용시 권한 없음 처리됨 )
     *   subject: userId, body: 톡인증 거래번호, 톡인증 권한 플레그
     *
     * - 톡인증 요청 url
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("IdPwAuthenticationFilter 통과 : {}",authResult.getName());

        UserContext userContext = (UserContext) authResult.getPrincipal();

        TalkToken accessToken = tokenFactory.createTalkAuthToken(userContext);

        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("token", accessToken.getToken());
        tokenMap.put("url", accessToken.getTalkAuthURL());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), tokenMap);

        response.isCommitted();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("IdPwAuthenticationFilter 실패 : {}",failed.getCause());
        SecurityContextHolder.clearContext();
        response.setStatus(401);
    }
}
