package com.dnai.cedre.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("!test")
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	private CustomCorsConfigurationSource customCorsConfigurationSource;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.cors().configurationSource(customCorsConfigurationSource)
				.and()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/**").permitAll()
				.anyRequest().authenticated()
		;
		log.debug("configuration cors and csrf");
	}
}
