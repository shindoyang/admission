package com.ut.security.vo;

import lombok.Data;

@Data
public class SmsCodeVo {
    private SmsVo oldSmsCode;
    private SmsVo newSmsCode;
}
