package com.dnai.cedre.config;

import com.dnai.cedre.util.ApplicationContextHolder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ComponentConfig {
	
	//@Autowired
	//private Environment environment;

    private static final String PREFIX = "[DNAI-CEDRE]";
	
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        log.debug("{} - Configuring application context holder", PREFIX);
        final ApplicationContextHolder applicationContextHolder = new ApplicationContextHolder();
        log.debug("{} - Finished configuring application context holder", PREFIX);
        return applicationContextHolder;
    }

    /*@Bean
    public AdnAiUtilsService adnAiUtilsService() {
        final EnvironmentProperties properties = EnvironmentProperties.newInstance(this.environment);
        return AdnAiUtilsServiceFactory.createAdnAiUtilsService(properties);
    }*/
}
