package com.ut.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 18:26 2017-11-14
 */

@SpringBootApplication
@EnableAuthorizationServer
@EnableDiscoveryClient
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableFeignClients
@Slf4j
public class OAuth2ServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(OAuth2ServiceApp.class, args);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
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
