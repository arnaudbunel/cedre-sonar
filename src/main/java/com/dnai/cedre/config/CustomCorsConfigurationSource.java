package com.dnai.cedre.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
public class CustomCorsConfigurationSource implements CorsConfigurationSource {
	@Value("${allow.origin}")
	private String[] allowedOrigins;
	
    private final CorsConfiguration configuration = new CorsConfiguration();

    @Override
    public CorsConfiguration getCorsConfiguration(final HttpServletRequest request) {
        return configuration;
    }

    @PostConstruct
    public void initCorsConfiguration() {
    	if(this.allowedOrigins != null) {
    	    log.debug("initCorsConfiguration: Allowed origins {}", String.join(",", this.allowedOrigins));
    	    configuration.setAllowedOrigins(Arrays.asList(this.allowedOrigins));
        }
    	
        configuration.addAllowedMethod(HttpMethod.GET);
        configuration.addAllowedMethod(HttpMethod.POST);

        configuration.addAllowedHeader("Content-Type");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedHeader("accept-language");
        configuration.addAllowedHeader("adnai-version");
    }
}