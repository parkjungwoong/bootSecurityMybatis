package com.myuoong.appAdmin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TalkAuthReturn {
    private String svcId;
    private String tNo;
    private String resultCd;
    private String msg;
    private String mac;
    private String userId;
    private String autoYn;

    @JsonCreator
    public TalkAuthReturn(@JsonProperty("svcId")String svcId, @JsonProperty("tNo")String tNo, @JsonProperty("resultCd")String resultCd
            , @JsonProperty("msg")String msg, @JsonProperty("mac")String mac, @JsonProperty("userId")String userId, @JsonProperty("autoYn")String autoYn) {
        this.svcId = svcId;
        this.tNo = tNo;
        this.resultCd = resultCd;
        this.msg = msg;
        this.mac = mac;
        this.userId = userId;
        this.autoYn = autoYn;
    }
}
