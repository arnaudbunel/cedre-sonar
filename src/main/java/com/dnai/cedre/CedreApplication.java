package com.dnai.cedre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class CedreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CedreApplication.class, args);
	}
	
	@Autowired
	private Environment env;
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
				String origin = env.getProperty(Constantes.ALLOW_ORIGIN);
				if(origin!=null) {
		           	String[] taborigin = origin.split(",");
					log.debug("addCorsMappings : origin : " +origin);
					// TODO à renforcer
	            	registry.addMapping("/**")
	            	.allowedOrigins(taborigin)
	            	.allowedMethods("POST","GET")
	            	.allowedHeaders("Content-Type","Authorization",Constantes.HEADER_ADNAI_VERSION, Constantes.HEADER_ADNAI_TOKEN);
				}else {
					log.warn("addCorsMappings : origin null");
				}

            }
        };
    }
}
