package com.myuoong.appAdmin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginReq {
    private String id;
    private String pw;
    private String path;
    private String token;

    @JsonCreator
    public LoginReq(@JsonProperty("id") String id, @JsonProperty("pw")String pw, @JsonProperty("path")String path, @JsonProperty("token")String token) {
        this.id = id;
        this.pw = pw;
        this.path = path;
        this.token = token;
    }
}
