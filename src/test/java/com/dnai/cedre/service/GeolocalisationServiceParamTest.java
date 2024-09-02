package com.dnai.cedre.service;

import com.dnai.cedre.config.ComponentConfig;
import com.dnai.cedre.model.PositionMdl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {ComponentConfig.class, GeolocalisationServiceParamTest.TestConfig.class})
@DisplayName("GeolocalisationServiceParam")
public class GeolocalisationServiceParamTest {

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

    @ParameterizedTest
    @CsvFileSource(resources = "/adresses-ti/adresses.csv", delimiter = ';')
	public void testGeocodeAdresseOk(String libadr) {
		
		PositionMdl positionMdl = geolocalisationService.geocodeAdresse(libadr);
		
		assertThat(positionMdl).isNotNull();
		assertThat(positionMdl.isTrouve()).isTrue();
	}
}
