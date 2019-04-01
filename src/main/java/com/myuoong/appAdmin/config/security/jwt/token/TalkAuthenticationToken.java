package com.myuoong.appAdmin.config.security.jwt.token;

import com.myuoong.appAdmin.model.TalkAuthReturn;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class TalkAuthenticationToken extends AbstractAuthenticationToken {

    private RawAccessJwtToken rawAccessToken;
    private TalkAuthReturn talkAuthReturn;

    public TalkAuthenticationToken(TalkAuthReturn talkAuthReturn, RawAccessJwtToken unsafeToken) {
        super(null);
        this.talkAuthReturn = talkAuthReturn;
        this.rawAccessToken = unsafeToken;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return rawAccessToken;
    }

    @Override
    public Object getPrincipal() {
        return this.talkAuthReturn;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.rawAccessToken = null;
    }
}
