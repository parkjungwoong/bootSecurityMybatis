package com.myuoong.appAdmin.config;

import com.myuoong.appAdmin.config.security.CustomUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity //Spring security filter chain 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    final String ID_PW_LOGIN_URL = "";
    final String TAUTH_FIN_URL = "";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(ID_PW_LOGIN_URL,TAUTH_FIN_URL)
                .permitAll()
                .and()
                .addFilterBefore()
                //todo : 1차 인증에 대한 필터 구현 및 등록 필요
    }
}
