package com.ut.user;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableJpaAuditing
@EnableFeignClients
@Slf4j
public class UserApp {
    public static void main(String args[]){
        SpringApplication.run(UserApp.class, args);
    }

    private static final String SITE_WIDE_SECRET = "my-secret-salt";
    @Bean
    PasswordEncoder passwordEncoder(){
        return new StandardPasswordEncoder(SITE_WIDE_SECRET);
    }

    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {

        return (RequestTemplate requestTemplate) -> {
            try {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                Authentication authentication = securityContext.getAuthentication();
                Object object = authentication.getDetails();
                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) object;
                requestTemplate.header("Authorization", "bearer " + details.getTokenValue());
            } catch (Exception ex) {
                log.info("token interceptor error");
            }
        };
    }
}
