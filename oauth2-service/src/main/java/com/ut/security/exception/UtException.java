package com.ut.security.exception;

import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@NoArgsConstructor
public class UtException extends Exception {

    private int utCode;
    private String utMsg;

    public UtException(UtExceptionEnum exEnum, String message) {
        super(message);
        this.utCode = exEnum.getUtCode();
        this.utMsg = message;
        log.error(this.toString());
    }

    public UtException(UtExceptionEnum exEnum) {
        this.utCode = exEnum.getUtCode();
        this.utMsg = exEnum.getUtMsg();
        log.error(this.toString());
    }

    public int getUtCode() {
        return utCode;
    }

    public String getUtMsg() {
        return utMsg;
    }
}
