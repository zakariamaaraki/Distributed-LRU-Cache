package com.zakaria.zcache.config;

import com.zakaria.zcache.models.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CacheConfig {
	
	@Value("${spring.application.capacity:1000000}")
	private int capacity;
	
	@Bean
	RestTemplate synchronize() {return new RestTemplate();}
	
	@Bean
	Cache<String,String> getMap() {
		return new Cache<>(capacity);
	}
}
