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
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/home/**").hasRole("USER")
                .antMatchers("/**").permitAll()
                .and()
                .csrf().disable()
                .formLogin().disable();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new CustomUserService();
    }
}
