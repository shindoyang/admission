package com.ut.security.model.vo;

import lombok.Data;

@Data
public class SmsVo {
    private String appPrefix;
    private String messageId;
    private String smsCode;
    private String phoneNo;
}
