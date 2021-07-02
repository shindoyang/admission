package com.ut.security.exception;

import com.alibaba.fastjson.JSON;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理：对自身异常类做指定返回结构
 */
@ControllerAdvice
@ResponseBody
public class WholeExceptionHandler {

    private final String UTSERVICE = "utService";
    private final String UTCODE = "utCode";
    private final String UTMSG = "utMsg";

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    @ExceptionHandler(UtException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map handleException(UtException e) {
        e.printStackTrace();
        Map map = new HashMap();
        map.put(UTSERVICE, SERVICE_NAME);
        map.put(UTCODE, e.getUtCode());
        map.put(UTMSG, e.getUtMsg());
        return map;
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map handleException(FeignException e) {
        e.printStackTrace();
        //只对自定义异常做转换
        Map map = new HashMap();
        String errorMsg = e.getMessage();
        if (errorMsg.contains(UTCODE)) {
            FeignExceptionVO vo = JSON.parseObject(errorMsg.substring(errorMsg.indexOf("{")), FeignExceptionVO.class);
            map.put(UTSERVICE, vo.getUtService());
            map.put(UTCODE, vo.getUtCode());
            map.put(UTMSG, vo.getUtMsg());
        } else {
            map.put(UTSERVICE, SERVICE_NAME);
            map.put(UTCODE, e.status());
            map.put(UTMSG, e.getMessage());
        }
        return map;
    }

    //这是实体类 参数校验注解不通过会抛出的异常 只有全局异常能拦截到
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//如果不指定响应码，默认返回的是200
    public Map handleIllegalParamException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        StringBuffer errorMsg = new StringBuffer();
        Map map = new HashMap();
        for (FieldError error : ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors()) {
            errorMsg.append(error.getField())
                    .append(":")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }
        map.put(UTSERVICE, SERVICE_NAME);
        map.put(UTCODE, UtExceptionEnum.PARAM_NOT_NULL.getUtCode());
        map.put(UTMSG, errorMsg.toString());
        return map;
    }

}
