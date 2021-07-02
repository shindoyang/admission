package com.ut.security.model.vo;

import lombok.Data;

@Data
public class SmsCodeVo {
    private SmsVo oldSmsCode;
    private SmsVo newSmsCode;
}
