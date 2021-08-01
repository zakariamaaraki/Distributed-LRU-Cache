package com.zakaria.zcache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
	
	@Bean
	public Docket cacheApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("cacheApi")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.zakaria.zcache.controllers"))
				.paths(PathSelectors.ant("/cache/api/*"))
				.build()
				.apiInfo(new ApiInfoBuilder().version("1.0").title("ZCache API").build());
	}
	
	@Bean
	public Docket healthApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("healthApi")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.zakaria.zcache.controllers"))
				.paths(PathSelectors.ant("/health/*"))
				.build()
				.apiInfo(new ApiInfoBuilder().version("1.0").title("ZCache Health API").build());
	}
}
