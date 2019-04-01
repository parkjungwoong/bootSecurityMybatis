package com.myuoong.appAdmin.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myuoong.appAdmin.common.util.CommonUtils;
import com.myuoong.appAdmin.config.security.jwt.token.*;
import com.myuoong.appAdmin.model.TalkAuthReturn;
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
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.myuoong.appAdmin.common.ComConst.*;

/**
 * 2차 인증에서 사용하는 필터
 */
@Slf4j
public class TalkAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JwtTokenFactory tokenFactory;

    public TalkAuthenticationFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper, JwtTokenFactory jwtTokenFactory) {
        super(defaultFilterProcessesUrl);
        this.objectMapper = objectMapper;
        this.tokenFactory = jwtTokenFactory;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        log.info("TalkAuthenticationFilter start");

        //요청 형식 체크
        if(!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.info("Authentication method not supported. Request method: " + request.getMethod());
            throw new AuthenticationServiceException("use post");
        }

        //토큰 있는지 검사
        String tokenPayload = request.getHeader(AUTHENTICATION_HEADER_NAME);

        if(tokenPayload == null){
            log.info("톡 인증 토큰 누락");
            throw new AuthenticationServiceException("톡 인증 토큰 누락");
        }

        //인증 파라미터 체크
        TalkAuthReturn talkAuth = objectMapper.readValue(request.getReader(), TalkAuthReturn.class);

        if( StringUtils.isBlank(talkAuth.getUserId())
                || StringUtils.isBlank(talkAuth.getSvcId())
                || StringUtils.isBlank(talkAuth.getTNo())
                || StringUtils.isBlank(talkAuth.getResultCd())
                || StringUtils.isBlank(talkAuth.getMac()) ){
            log.info("인증 파라미터 누락");
            throw new AuthenticationServiceException("인증 파라미터 누락");
        }

        RawAccessJwtToken token = new RawAccessJwtToken(CommonUtils.extract(tokenPayload));

        return getAuthenticationManager().authenticate(new TalkAuthenticationToken(talkAuth,token));
    }

    /**
     * 2차 인증 성공시 핸들러, 응답값 세팅 후 클라이언트에 응답.
     * 응답값
     * - 엑세스 토큰 ( api 자원에 요청시 사용 )
     *   subject: userId, body: svcId, phone, 로그인 사용자 권한
     *
     * - 리프레시 토큰 ( 엑세스 토큰 갱신시 사용 )
     *   subject: userId, body: 리프레시 권한
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //2차 인증까지 완료 후 api요청에 접근할 수 있는 토큰발급
        UserContext userContext = (UserContext) authResult.getPrincipal();

        JwtToken accessToken = tokenFactory.createAccessJwtToken(userContext);
        JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);

        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put(JWT_KEY_ACCESS_TOKEN, accessToken.getToken());
        tokenMap.put(JWT_KEY_REFRESH_TOKEN, refreshToken.getToken());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), tokenMap);

        log.info("TalkAuthenticationFilter 통과");
        response.isCommitted();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("TalkAuthenticationFilter 실패 : {}",failed.getCause());
        SecurityContextHolder.clearContext();
        response.setStatus(401);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) return;

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
