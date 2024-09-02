package com.dnai.cedre.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.dnai.cedre.CedreApplicationTI;

@ActiveProfiles("test")
@SpringBootTest(classes = {CedreApplicationTI.class})
@Disabled
public class TrajetServiceTest {

    @TestConfiguration
    static class TrajetServiceTestContextConfiguration {
  
        @Bean
        public TrajetService trajetService() {
            return new TrajetService();
        }
    }
	
    @Autowired
	private TrajetService trajetService;
    
	@Test
	public void formatDistance_ok1() {
		Long distance = 12345L;
		String distanceFmt = trajetService.formatDistance(distance);
		assertThat(distanceFmt).isNotNull();
		assertThat(distanceFmt).isEqualTo("12,3");
	}
}
