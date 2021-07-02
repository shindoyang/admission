package com.ut.security.config;

import com.alibaba.fastjson.JSON;
import com.ut.security.model.CustomExceptionDTO;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;


/**
 * @author chenglin
 * 抓取feign异常转为自定义异常
 */
@Slf4j
public class UtFeignExceptionConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new UtErrorDecoder();
    }

    public static class UtErrorDecoder extends ErrorDecoder.Default {
        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                int status = response.status();
                if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    String body = IOUtils.toString(response.body().asReader());
                    CustomExceptionDTO error = JSON.parseObject(body, CustomExceptionDTO.class);
                    if (error.getMessage().contains("Exception:")) {
                        int local = error.getMessage().indexOf(":");
                        return new BadCredentialsException(error.getMessage().substring(local+2,error.getMessage().length()-1));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return super.decode(methodKey, response);
        }
    }

}
