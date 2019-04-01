package com.myuoong.appAdmin.controller;

import com.myuoong.appAdmin.config.security.jwt.token.JwtTokenFactory;
import com.myuoong.appAdmin.model.User;
import com.myuoong.appAdmin.model.UserContext;
import com.myuoong.appAdmin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.myuoong.appAdmin.common.ComConst.RULE_SPLIT_STR;

@RequestMapping("/")
@RestController
public class ComController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtTokenFactory tokenFactory;

    //엑세스 토큰 새로 발급 요청 처리
    @RequestMapping("/api/refreshToken")
    public String refreshToken(Authentication authentication){
        //유저 컨텍스트 가져오기
        final UserContext userContext = (UserContext)authentication.getPrincipal();

        //자동 로그인 권한일 경우 인증 정보 테이블에서 사용가능한 토큰인지 검사 - 옵션 사항 ( 한군대에서만 자동 로그인 가능하게 한다면.. )

        //자동 로그인 권한인데 사용중인 자동 로그인 토큰이 2개 이상이면 가장 최근것만 남겨두고 나머진 사용 불가 처리 -> 사용 불가 처리된건은 엑세스 토큰 새로 발급 받을떄 엑세스 디나인 - 옵션 사항 ( 한군대에서만 자동 로그인 가능하게 한다면.. )

        //유저 정보 새로 조회 후 세팅
        User user = service.selectUserById(userContext.getUserId());

        //유저 정보 없을때 에러 처리
        if(user == null) throw new UsernameNotFoundException("유저 정보 없음, userId : "+userContext.getUserId());

        //새로운 엑세스 토큰 생성 후 반환
        String[] userScopes = user.getRole().split(RULE_SPLIT_STR);

        List<GrantedAuthority> authorities = Arrays.stream(userScopes)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserContext refreshUser = UserContext.create(user,authorities);

        return tokenFactory.createAccessJwtToken(refreshUser).getToken();
    }

    @RequestMapping("/test")
    public Map test(){
        Map res = new HashMap();
        res.put("test","value");

        return res;
    }

}
