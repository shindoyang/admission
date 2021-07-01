package com.ut.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Map;

/**
 * @auth: 陈佳攀
 * @Description:
 * @Date: Created in 14:55 2017-11-24
 */

@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/druid/**", "/public/**").permitAll()
                .antMatchers("/**").authenticated()
                .anyRequest().authenticated().and().csrf().disable();
    }
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("user").tokenStore(tokenStore());
    }

    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        CustomJwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    /**
     * 自定义JwtAccessTokenConverter，用于构造解析OAuth2Authentication时缓存额外信息到Details中
     */
    public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

        @Override
        public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
            OAuth2Authentication authentication = super.extractAuthentication(claims);
            authentication.setDetails(claims);
            return authentication;
        }
    }
}
