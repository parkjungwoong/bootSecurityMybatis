package com.myuoong.appAdmin.config.security;

import com.myuoong.appAdmin.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

public class CustomUserService implements UserDetailsService {

    static final Logger log = LoggerFactory.getLogger(CustomUserService.class);

    @Autowired
    private TestService service;

    @Override
    public CustomUserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("login id : {} ",s);

        Map user = service.getUserById(s);

        if(user == null) throw new UsernameNotFoundException("user not found id => "+s);

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setId((String)user.get("ID"));
        customUserDetails.setPw((String)user.get("PW"));
        customUserDetails.setName((String)user.get("NAME"));
        customUserDetails.setExp((boolean)user.get("IS_EXP"));
        customUserDetails.setLock((boolean)user.get("IS_LOCK"));
        customUserDetails.setEnable((boolean)user.get("IS_ENABLE"));
        customUserDetails.setRole((String)user.get("ROLE"));

        return customUserDetails;
    }
}
