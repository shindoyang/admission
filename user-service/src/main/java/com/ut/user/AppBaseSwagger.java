package com.ut.user;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@EnableSwagger2
@Configuration
public class AppBaseSwagger {
    @Bean
    public Docket testApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .genericModelSubstitutes(DeferredResult.class)
//                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
//                .forCodeGeneration(true)
                .pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select()
                .apis(RequestHandlerSelectors.basePackage("com"))
                .paths(or(regex("/*.*")))//过滤的接口
                .build()
                .apiInfo(testApiInfo());
    }
    private ApiInfo testApiInfo() {
        return new ApiInfoBuilder()
                .description("EHR Platform's REST API, all the applications could access the Object model data via JSON.")//详细描述
                .version("1.0")//版本
                .contact(new Contact("陈佳攀", "13758191975", "25143976@qq.com"))//作者
                .build();
    }


    @Configuration
    @SuppressWarnings("SpringJavaAutowiringInspection")
    static public class SpringDataConfiguration {

        @Bean
        public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
            return new AlternateTypeRuleConvention() {
                @Override
                public int getOrder() {
                    return Ordered.HIGHEST_PRECEDENCE;
                }

                @Override
                public List<AlternateTypeRule> rules() {
                    return newArrayList(newRule(resolver.resolve(Pageable.class), resolver.resolve(SpringDataConfiguration.Page.class)));
                }
            };
        }

        @ApiModel
        static class Page {
            @ApiModelProperty("Results page you want to retrieve (0..N)")
            private Integer page;

            @ApiModelProperty("Number of records per page")
            private Integer size;

            @ApiModelProperty("Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")
            private List<String> sort;

            public Integer getPage() {
                return page;
            }

            public void setPage(Integer page) {
                this.page = page;
            }

            public Integer getSize() {
                return size;
            }

            public void setSize(Integer size) {
                this.size = size;
            }

            public List<String> getSort() {
                return sort;
            }

            public void setSort(List<String> sort) {
                this.sort = sort;
            }
        }
    }
}
