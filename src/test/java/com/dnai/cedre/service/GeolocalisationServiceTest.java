package com.dnai.cedre.service;

import com.dnai.cedre.config.ComponentConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ComponentConfig.class, GeolocalisationServiceTest.TestConfig.class})
public class GeolocalisationServiceTest {

    @Configuration
    static class TestConfig {
  
        @Bean
        public GeolocalisationService geolocalisationService() {
            return new GeolocalisationService();
        }

		@Bean
		public CommonUtilService commonUtilService() {
			return new CommonUtilService();
		}
    }
	
    @Autowired
	private GeolocalisationService geolocalisationService;
	
	@Test
	public void calculDistance1() {
		String origin = "46.082684, -1.049755111112";
		String destination = "46.080399, -1.054240";
		
		long distance = geolocalisationService.calculDistance(origin, destination);
		assertThat(distance).isGreaterThan(0);
	}
	
	@Test
	public void calculDistance2() {
		String origin = "0.0, 0.0";
		String destination = "46.080399, -1.054240";
		
		long distance = geolocalisationService.calculDistance(origin, destination);
		assertThat(distance).isEqualTo(0);
	}
	
	@Test
	public void isOriginDestinationOk1(){
		String origin = "0.0, 0.0";
		String destination = "46.080399, -1.054240";
		boolean isOriginDestinationOk = geolocalisationService.isOriginDestinationOk(origin, destination);
		assertThat(isOriginDestinationOk).isFalse();
	}
	
	@Test
	public void isOriginDestinationOk2(){
		String origin = "46.082684, -1.049755111112";
		String destination = "46.080399, -1.054240";
		boolean isOriginDestinationOk = geolocalisationService.isOriginDestinationOk(origin, destination);
		assertThat(isOriginDestinationOk).isTrue();
	}
}
