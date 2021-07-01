package com.ut.user.vo;

import lombok.Data;

@Data
public class SmsVo {
    private String appPrefix;
    private String messageId;
    private String smsCode;
    private String phoneNo;
}
