package com.myuoong.appAdmin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class LoginReq {
    private String id;
    private String pw;
    private String path;
    private String token;

    @JsonCreator
    public LoginReq(String id, String pw, String path, String token) {
        this.id = id;
        this.pw = pw;
        this.path = path;
        this.token = token;
    }
}
