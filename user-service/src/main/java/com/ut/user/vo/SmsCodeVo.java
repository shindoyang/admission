package com.ut.user.vo;

import lombok.Data;

@Data
public class SmsCodeVo {
    private SmsVo oldSmsCode;
    private SmsVo newSmsCode;
}
