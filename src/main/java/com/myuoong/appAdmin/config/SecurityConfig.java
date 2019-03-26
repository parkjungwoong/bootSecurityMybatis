package com.myuoong.appAdmin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myuoong.appAdmin.config.security.RestAuthenticationEntryPoint;
import com.myuoong.appAdmin.config.security.filter.LoginAuthenticationFilter;
import com.myuoong.appAdmin.config.security.filter.TalkAuthenticationFilter;
import com.myuoong.appAdmin.config.security.provider.IdPwAuthenticationProvider;
import com.myuoong.appAdmin.config.security.provider.TAuthFinAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //Spring security filter chain 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired private IdPwAuthenticationProvider idPwAuthenticationProvider;
    @Autowired private TAuthFinAuthenticationProvider tAuthFinAuthenticationProvider;
    @Autowired private ObjectMapper objectMapper;

    final String ID_PW_LOGIN_URL = "/idPwLogin";
    final String TAUTH_FIN_URL = "/tauthFin";
    final String API_ROOT_URL = "/api/**";

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable() // We don't need CSRF for JWT based authentication
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                //권한 없이 접근 가능한 URL
                .authorizeRequests()
                .antMatchers(ID_PW_LOGIN_URL,TAUTH_FIN_URL)
                .permitAll()
                .and()

                //권한이 필요한 URL
                .authorizeRequests()
                .antMatchers(API_ROOT_URL).authenticated()
                .and()

                //1차 인증 관련 필터
                .addFilterBefore(loginFilterBuilder(ID_PW_LOGIN_URL), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(idPwAuthenticationProvider)

                //2차 인증 관련 필터
                .addFilterBefore(talkAuthFilterBuilder(TAUTH_FIN_URL), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(tAuthFinAuthenticationProvider)
                ;
    }

    //1차 인증 필터 생성
    private LoginAuthenticationFilter loginFilterBuilder(final String url){
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter(url,objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //2치 인증 필터
    private TalkAuthenticationFilter talkAuthFilterBuilder(final String url){
        TalkAuthenticationFilter filter = new TalkAuthenticationFilter(url);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    //프로바이더 등록 (인증 요청을 검증하는 클래스들 - 프로바이더 매니저가 등록된 순서대로 루프를 돌며 처리한다)
   /* @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(idPwAuthenticationProvider);
        auth.authenticationProvider(tAuthFinAuthenticationProvider);
    }*/
}
