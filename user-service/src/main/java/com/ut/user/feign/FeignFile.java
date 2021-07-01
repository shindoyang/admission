package com.ut.user.feign;

import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "file-service", url = "${platform.service-url.file}",
        configuration = FeignFile.ClientConfiguration.class)
public interface FeignFile {

    @RequestMapping(
            value = "/qiniu/server_upload",
            method = POST,
            consumes = MULTIPART_FORM_DATA_VALUE
    )
    void upload1 (@RequestPart(value = "file") MultipartFile file,
                    @RequestParam(value = "appKey") String appKey);


    class ClientConfiguration {

        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        public Encoder feignEncoder () {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }

        @Bean
        public Logger.Level feignLogger () {
            return Logger.Level.FULL;
        }
    }
}

