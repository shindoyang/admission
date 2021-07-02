package com.ut.security.exception;

import lombok.Data;

@Data
public class FeignExceptionVO {
    private int utCode;
    private String utMsg;
    private String utService;
}
