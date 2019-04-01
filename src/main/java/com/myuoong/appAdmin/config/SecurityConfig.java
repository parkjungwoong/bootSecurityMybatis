package com.myuoong.appAdmin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myuoong.appAdmin.config.security.RestAuthenticationEntryPoint;
import com.myuoong.appAdmin.config.security.filter.IdPwAuthenticationFilter;
import com.myuoong.appAdmin.config.security.filter.ResourceAuthenticationFilter;
import com.myuoong.appAdmin.config.security.filter.TalkAuthenticationFilter;
import com.myuoong.appAdmin.config.security.jwt.token.JwtTokenFactory;
import com.myuoong.appAdmin.config.security.provider.IdPwAuthenticationProvider;
import com.myuoong.appAdmin.config.security.provider.JwtAuthenticationProvider;
import com.myuoong.appAdmin.config.security.provider.TalkAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.myuoong.appAdmin.common.ComConst.*;

/**
 * Spring security 설정 클래스
 *
 * 작성 패턴
 * ^Provider : 사용자 검증을 담당하는 클래스
 *      사용자 입력값과 DB에서 조회한 값을 비교
 *      인증 실패시 AuthenticationException 발생
 *      성공시 사용자 정보 반환
 *
 * ^AuthenticationFilter : 요청값 유효성 검증을 담당하는 클래스
 *      필수 입력값 검사
 *      http 요청 형식 검사
 *      인증 성공, 실패 핸들러 지정
 *
 * security 동작 순서 ( provider,filter 기준 )
 *      ^AuthenticationFilter.attemptAuthentication() -> ^Provider.support() -> ^Provider.authenticate() -> ^AuthenticationFilter.successfulAuthentication() or ^AuthenticationFilter.unsuccessfulAuthentication()
 *      successfulAuthentication 메소드에서 doFilter() 호출되는 부분이 없으면 controller로 안넘어감
 *
 * 인증 관련 응답 형식 : http 상태 코드로 응답
 *      인증 실패 : 401
 *      인증 성공 : 200
 *
 * 인증 프로세스 : 1차, 2차 인증 후 jwt 토큰 발급
 *      1차(id/pw) -> 톡인증 요청권한을 갖는 엑세스 토큰 발급 -> 2차(톡인증) -> api 엑세스/리프레쉬 토큰 발급 -> api 엑세스 토큰으로 자원에 접급, 엑세스 토큰 기간이 끝나면 리프레쉬 토큰으로 재발급, 라프레쉬 토큰 기간도 끝나면 다시 1차 로그인부터 시작
 *
 */
@Configuration
@EnableWebSecurity //Spring security filter chain 추가
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private AuthenticationManager authenticationManager;
    //@Autowired private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired private IdPwAuthenticationProvider idPwAuthenticationProvider;
    @Autowired private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Autowired private TalkAuthenticationProvider talkAuthenticationProvider;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenFactory jwtTokenFactory;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("create custom security config");

        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                //권한 없이 접근 가능한 URL
                .authorizeRequests()
                .antMatchers(ID_PW_LOGIN_URL, TAUTH_FIN_URL)
                .permitAll()
                .and()

                //권한이 필요한 URL
                .authorizeRequests()
                .antMatchers(API_ROOT_URL).authenticated()
                .and()

                //1차 인증 관련 필터
                .addFilterBefore(loginFilterBuilder(), UsernamePasswordAuthenticationFilter.class)
                //2차 인증 관련 필터
                .addFilterBefore(talkAuthFilterBuilder(), UsernamePasswordAuthenticationFilter.class)
                //자원 요청 관련 필터
                .addFilterBefore(resourceAuthFilterBuilder(), UsernamePasswordAuthenticationFilter.class)
                ;
    }

    //1차 인증 필터 생성
    private IdPwAuthenticationFilter loginFilterBuilder(){
        IdPwAuthenticationFilter filter = new IdPwAuthenticationFilter(ID_PW_LOGIN_URL,objectMapper,jwtTokenFactory);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //2치 인증 필터
    private TalkAuthenticationFilter talkAuthFilterBuilder(){
        TalkAuthenticationFilter filter = new TalkAuthenticationFilter(TAUTH_FIN_URL,objectMapper,jwtTokenFactory);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //자원 요청 관련 필터
    private ResourceAuthenticationFilter resourceAuthFilterBuilder(){
        ResourceAuthenticationFilter filter = new ResourceAuthenticationFilter(API_ROOT_URL);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //프로바이더 등록 (인증 요청을 검증하는 클래스들 - 프로바이더 매니저가 등록된 순서대로 루프를 돌며 처리한다)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider);
        auth.authenticationProvider(idPwAuthenticationProvider);
        auth.authenticationProvider(talkAuthenticationProvider);
    }
}
