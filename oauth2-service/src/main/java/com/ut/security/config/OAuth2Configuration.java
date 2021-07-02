package com.ut.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Arrays;


@Configuration
public class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private UserDetailsService myUserDetailService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Value("${spring.oauth2.client.validity_seconds.access_token}")
	private int accessTokenValiditySeconds;
	@Value("${spring.oauth2.client.validity_seconds.refresh_token}")
	private int refreshTokenValiditySeconds;
	@Value("${spring.oauth2.client.refresh_token.reuse}")
	private boolean refreshTokenReuse;

	/**
	 * 客户端配置
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
		clients.inMemory()
				.withClient("sso-gateway")
				.secret(passwordEncoder.encode("sso-gateway-secret"))
				.authorizedGrantTypes("refresh_token", "authorization_code", "password")
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds)//30天 单位:秒
				.scopes("read").autoApprove(true)
				.and()
				.withClient("mobile-client")
				.secret(passwordEncoder.encode("mobile-secret"))
				.authorizedGrantTypes("refresh_token", "authorization_code", "password")
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds)
				.scopes("read").autoApprove(true)
				.and()
				.withClient("wulian-client")
				.secret(passwordEncoder.encode("wulian-secret"))
				.authorizedGrantTypes("refresh_token", "authorization_code", "password")
				.accessTokenValiditySeconds(accessTokenValiditySeconds)
				.refreshTokenValiditySeconds(refreshTokenValiditySeconds)
				.scopes("read").autoApprove(true);
	}

	/**
	 * tokenKey的访问权限表达式配置
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()");
	}

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(
				Arrays.asList(customTokenEnhancer(), jwtAccessTokenConverter()));

		endpoints.tokenStore(tokenStore())
				.tokenEnhancer(jwtAccessTokenConverter())
				.authenticationManager(authenticationManager)
				.userDetailsService(myUserDetailService)
				.tokenEnhancer(tokenEnhancerChain)
				.tokenServices(tokenServices())
				;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	@Bean
	protected JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey("123");
		return jwtAccessTokenConverter;
	}

	@Bean
	public TokenEnhancer customTokenEnhancer() {
		return new CustomTokenEnhancer();
	}

    /*public static void main(String[] args) {
        //Http Basic 验证
        String clientAndSecret = "wulian-client:wulian-secret";
        clientAndSecret = "Basic " + Base64.getEncoder().encodeToString(clientAndSecret.getBytes());
        System.out.println(clientAndSecret);
    }*/

	@Primary
	@Bean
	public AuthorizationServerTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setReuseRefreshToken(refreshTokenReuse);
		defaultTokenServices.setClientDetailsService(clientDetailsService);
		defaultTokenServices.setTokenEnhancer(getTokenEnhancer());
		defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
		defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
		addUserDetailsService(defaultTokenServices,myUserDetailService);
		return defaultTokenServices;
	}

	public TokenEnhancer getTokenEnhancer() {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer(), jwtAccessTokenConverter()));
		return tokenEnhancerChain;
	}


	private void addUserDetailsService(DefaultTokenServices tokenServices, UserDetailsService userDetailsService) {
		if (userDetailsService != null) {
			PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
			provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>(
					userDetailsService));
			tokenServices.setAuthenticationManager(new ProviderManager(Arrays.<AuthenticationProvider>asList(provider)));
		}
	}
}