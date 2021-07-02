package com.ut.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @author litingting
 */
@EnableSwagger2
@Configuration
public class AppBaseSwagger {
	@Bean
	public Docket testApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("oauth")
				.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false)
				// base，最终调用接口后会和paths拼接在一起
				.pathMapping("/")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.ut.security.controller"))
				//过滤的接口
				.paths((regex("/*.*")))
				.build()
				.apiInfo(testApiInfo());
	}

	private ApiInfo testApiInfo() {
		return new ApiInfoBuilder()
				.description("Oauth测试接口")
				.version("1.0")
				.contact(new Contact("陈佳攀", "13758191975", "25143976@qq.com"))
				.build();
	}
}
