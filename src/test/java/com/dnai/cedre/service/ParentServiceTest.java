package com.dnai.cedre.service;

import com.dnai.cedre.config.ComponentConfig;
import com.dnai.cedre.dao.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(JUnitPlatform.class)
@ActiveProfiles("test")
//@SpringBootTest(classes = {CedreApplicationTI.class})
@SpringBootTest(classes = {ComponentConfig.class, ParentServiceTest.TestConfig.class})
public class ParentServiceTest {

    /*@TestConfiguration
    static class TourneeSuiviServiceTestContextConfiguration {
  
        @Bean
        public ParentService parentService() {
            return new ParentService();
        }
    }*/
	
    @Autowired
	private ParentService parentService;

	@Test
	public void traitementPdsOse_ok1() {
		assertThat(parentService.traitementPdsOse("?")).isEqualTo("0");
	}
	
	@Test
	public void traitementPdsOse_ok2() {
		assertThat(parentService.traitementPdsOse("????")).isEqualTo("0");
	}
	
	@Test
	public void traitementPdsOse_ok3() {
		assertThat(parentService.traitementPdsOse("1.23")).isEqualTo("1.23");
	}
	
	@Test
	public void parseDouble_ok1() {
		assertThat(parentService.parseDouble("1.23")).isEqualTo(1.23);
	}
	
	@Test
	public void parseDouble_ok2() {
		assertThat(parentService.parseDouble(null)).isEqualTo(0);
	}
	
	@Test
	public void formatTel_ok1() {
		assertThat(parentService.formatTel("01 40 17 88 34")).isEqualTo("+33140178834");
	}
	
	@Test
	public void formatTel_ok2() {
		assertThat(parentService.formatTel("01 40 17 86 00/59")).isNull();
	}
	
	@Test
	public void formatTel_ok3() {
		assertThat(parentService.formatTel("05.46.42.25.01")).isEqualTo("+33546422501");
	}
	
	@Test
	public void formatPoids_ok1() {
		assertThat(parentService.formatPoids(1.5)).isEqualTo("1,5");
	}
	
	@Test
	public void formatPoids_ok2() {
		assertThat(parentService.formatPoids(0.5)).isEqualTo("0,5");
	}
	
	@Test
	public void formatPoids_ok3() {
		assertThat(parentService.formatPoids(150)).isEqualTo("150");
	}
	
	@Test
	public void parseInt_ok1() {
		assertThat(parentService.parseInt("1500")).isEqualTo(1500);
	}
	
	@Test
	public void parseInt_ok2() {
		assertThat(parentService.parseInt("quinze ?")).isEqualTo(0);
	}

	@Configuration
	static class TestConfig {
		@Bean
		public ParentService parentService() {
			// return Mockito.mock(ParentService.class);
			return new ParentService();
		}


		@Bean
		public PrestationRepository prestationRepository() {
			return Mockito.mock(PrestationRepository.class);
		}

		@Bean
		public ClientoseRepository clientoseRepository() {
			return Mockito.mock(ClientoseRepository.class);
		}

		@Bean
		public UtilisateurGroupesiteRepository utilisateurGroupesiteRepository() {
			return Mockito.mock(UtilisateurGroupesiteRepository.class);
		}

		@Bean
		public GroupesiteClientoseRepository groupesiteClientoseRepository() {
			return Mockito.mock(GroupesiteClientoseRepository.class);
		}

		@Bean
		public CollecteRepository collecteRepository() {
			return Mockito.mock(CollecteRepository.class);
		}

		@Bean
		public TourneeUtilService tourneeUtilService() {
			return Mockito.mock(TourneeUtilService.class);
		}

		@Bean
		public GeolocalisationService geolocalisationService() {
			return Mockito.mock(GeolocalisationService.class);
		}
		@Bean
		public ProfileUtilService profileUtilService() {
			return Mockito.mock(ProfileUtilService.class);
		}

		@Bean
		public CommonUtilService commonUtilService() {
			return Mockito.mock(CommonUtilService.class);
		}
	}
}
