package com.myuoong.appAdmin.controller;

import com.myuoong.appAdmin.AppAdminApplication;
import com.myuoong.appAdmin.config.SecurityConfig;
import com.myuoong.appAdmin.config.security.filter.ResourceAuthenticationFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {AppAdminApplication.class,SecurityConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
//@WebMvcTest()
@WebAppConfiguration
public class ComController {
    /*@Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private MockMvc mockMvc;*/

    @Before
    public void setup() throws Exception{
        /*DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        MockFilterConfig secFilterConfig = new MockFilterConfig(context.getServletContext(),
                BeanIds.SPRING_SECURITY_FILTER_CHAIN);
        delegateProxyFilter.init(secFilterConfig);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                //.addFilters(springSecurityFilterChain)
                //.addFilter(delegateProxyFilter)
                .build();*/
    }

    @Test
    public void refeshToken요청테스트() throws Exception{
        HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.set("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInNjb3BlcyI6WyJST0xFX0FETUlOIl0sInN2Y0lkIjoiNzc4OCIsInBob25lIjoiMDEwODQyMjYzMTgiLCJpc3MiOiJtb2JpbGlhbnMiLCJpYXQiOjE1NTM5NDA2OTEsImV4cCI6MTU1Mzk0MDk5MX0.pZM_Qz-1ge-hwPwWSY1LiS18Yd6tLUuQu1XJ0OYmXwxDO4LZJzWjvdidIlmJg94iqahuA0_uRtOMmHct4_Rl3g1");
        httpHeaders.set("Content-Type","application/json");

        /*this.mockMvc.perform(post("/api/refreshToken")
                .header(HttpHeaders.AUTHORIZATION,"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInNjb3BlcyI6WyJST0xFX0FETUlOIl0sInN2Y0lkIjoiNzc4OCIsInBob25lIjoiMDEwODQyMjYzMTgiLCJpc3MiOiJtb2JpbGlhbnMiLCJpYXQiOjE1NTM5NDA2OTEsImV4cCI6MTU1Mzk0MDk5MX0.pZM_Qz-1ge-hwPwWSY1LiS18Yd6tLUuQu1XJ0OYmXwxDO4LZJzWjvdidIlmJg94iqahuA0_uRtOMmHct4_Rl3g1")
                .headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk());*/
    }


}
