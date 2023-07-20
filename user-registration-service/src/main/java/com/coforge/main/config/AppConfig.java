package com.coforge.main.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setReadTimeout(5000);	// Wait 5 sec for response from 3rd Party API
		httpRequestFactory.setConnectTimeout(5000); // Wait 5 sec for making connection
		return new RestTemplate(httpRequestFactory);
	}

	@Bean
	public WebClient webClient(){
		return WebClient.builder().build();
	}
}
