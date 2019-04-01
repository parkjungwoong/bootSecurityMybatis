package com.myuoong.appAdmin.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * 
 * @author vladimir.stankovic
 *
 * Aug 4, 2016
 */
@Getter
public class UserContext {
    private final String userId;
    private final User user;
    private final List<GrantedAuthority> authorities;
    private final TalkAuthReturn talkAuthReturn;

    private UserContext(String userId, List<GrantedAuthority> authorities, User user, TalkAuthReturn talkAuthReturn ) {
        this.userId = userId;
        this.authorities = authorities;
        this.user = user;
        this.talkAuthReturn = talkAuthReturn;
    }

    public static UserContext create(String username, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(username)) throw new IllegalArgumentException("Username is blank:");
        User u = new User();
        u.setId(username);
        return new UserContext(username, authorities, u, null);
    }

    public static UserContext create(User user, List<GrantedAuthority> authorities) {
        if (StringUtils.isBlank(user.getId())) throw new IllegalArgumentException("Username is blank:");
        return new UserContext(user.getId(),authorities,user,null);
    }

    public static UserContext create(TalkAuthReturn talkAuthReturn) {
        if (StringUtils.isBlank(talkAuthReturn.getUserId())) throw new IllegalArgumentException("Username is blank");
        User u = new User();
        u.setId(talkAuthReturn.getUserId());
        return new UserContext(talkAuthReturn.getUserId(),null,u,talkAuthReturn);
    }
}
