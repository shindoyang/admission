package com.ut.user.exception;

import lombok.Data;

@Data
public class FeignExceptionVO {
    private int utCode;
    private String utMsg;
    private String utService;
}
