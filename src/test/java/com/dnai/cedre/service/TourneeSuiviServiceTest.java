package com.dnai.cedre.service;

import com.dnai.cedre.config.AmazonTestConfig;
import com.dnai.cedre.config.ComponentConfig;
import com.dnai.cedre.dao.*;
import com.dnai.cedre.model.PositionevtMdl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

// @RunWith(JUnitPlatform.class)
@ActiveProfiles("test")
// @SpringBootTest(classes = {CedreApplicationTI.class})
@SpringBootTest(classes = {AmazonTestConfig.class, ComponentConfig.class, TourneeSuiviServiceTest.TestConfig.class})
public class TourneeSuiviServiceTest {

    @Configuration
    static class TestConfig {
  
        @Bean
        public TourneeSuiviService tourneeSuiviService() {
            return new TourneeSuiviService();
        }

		@Bean
		public TourneeRepository tourneeRepository() {
			return Mockito.mock(TourneeRepository.class);
		}

		@Bean
		public PrestationRepository prestationRepository() {
			return Mockito.mock(PrestationRepository.class);
		}

		@Bean
		public CollecteRepository collecteRepository() {
			return Mockito.mock(CollecteRepository.class);
		}

		@Bean
		public SignalementRepository signalementRepository() {
			return Mockito.mock(SignalementRepository.class);
		}

		@Bean
		public PositionevtRepository positionevtRepository() {
			return Mockito.mock(PositionevtRepository.class);
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
		public TourneeUtilService tourneeUtilService() {
			return Mockito.mock(TourneeUtilService.class);
		}

		@Bean
		public GeolocalisationService geolocalisationService() {
			return new GeolocalisationService();
		}

		@Bean
		public ProfileUtilService profileUtilService() {
			return Mockito.mock(ProfileUtilService.class);
		}

		@Bean
		public CommonUtilService commonUtilService() {
			return new CommonUtilService();
		}

		@Bean
		public MediaService mediaService() {
			return Mockito.mock(MediaService.class);
		}

		@Bean
		public AvisdePassageService avisdePassageService() {
			return Mockito.mock(AvisdePassageService.class);
		}
		@Bean
		public MediaRepository mediaRepository() {
			return Mockito.mock(MediaRepository.class);
		}
		@Bean
		public MediaDibRepository mediaDibRepository() {
			return Mockito.mock(MediaDibRepository.class);
		}
	}
	
    @Autowired
	private TourneeSuiviService tourneeSuiviService;
    

	
	@Test
	@Disabled
	public void mapDistance1() {
		List<PositionevtMdl> positionsevt = new ArrayList<>();
		PositionevtMdl positionevt1 = new PositionevtMdl();
		positionevt1.setEvt("DEBUT_TOURNEE");
		positionevt1.setLatitude(46.082562);
		positionevt1.setLongitude(-1.049752);
		positionsevt.add(positionevt1);
		PositionevtMdl positionevt2 = new PositionevtMdl();
		positionevt2.setEvt("DEBUT_CLIENT:48173");
		positionevt2.setLatitude(46.083629);
		positionevt2.setLongitude(-1.049080);
		positionsevt.add(positionevt2);
		PositionevtMdl positionevt3 = new PositionevtMdl();
		positionevt3.setEvt("FIN_CLIENT:48173");
		positionevt3.setLatitude(46.083629);
		positionevt3.setLongitude(-1.049080);
		positionsevt.add(positionevt3);
		PositionevtMdl positionevt4 = new PositionevtMdl();
		positionevt4.setEvt("DEBUT_CLIENT:2254");
		positionevt4.setLatitude(46.087242);
		positionevt4.setLongitude(-1.061634);
		positionsevt.add(positionevt4);
		PositionevtMdl positionevt5 = new PositionevtMdl();
		positionevt5.setEvt("FIN_CLIENT:2254");
		positionevt5.setLatitude(46.087242);
		positionevt5.setLongitude(-1.061634);
		positionsevt.add(positionevt5);
		PositionevtMdl positionevt6 = new PositionevtMdl();
		positionevt6.setEvt("DEBUT_CLIENT:13712");
		positionevt6.setLatitude(46.082409);
		positionevt6.setLongitude(-1.074470);
		positionsevt.add(positionevt6);
		PositionevtMdl positionevt7 = new PositionevtMdl();
		positionevt7.setEvt("FIN_CLIENT:13712");
		positionevt7.setLatitude(46.082409);
		positionevt7.setLongitude(-1.074470);
		positionsevt.add(positionevt7);
		PositionevtMdl positionevt8 = new PositionevtMdl();
		positionevt8.setEvt("FIN_TOURNEE");
		positionevt8.setLatitude(46.082562);
		positionevt8.setLongitude(-1.049752);
		positionsevt.add(positionevt8);
		
		Map<String, Long> mapDistance = tourneeSuiviService.mapDistance(positionsevt);
		
		assertThat(mapDistance.size()).isEqualTo(4);
		assertThat(mapDistance.get("total")).isGreaterThan(0);
		
		System.out.println(mapDistance);
	}
	
	@Test
	@Disabled
	public void mapDistance2() {
		List<PositionevtMdl> positionsevt = new ArrayList<>();
		PositionevtMdl positionevt1 = new PositionevtMdl();
		positionevt1.setEvt("DEBUT_TOURNEE");
		positionevt1.setLatitude(48.76117706);
		positionevt1.setLongitude(2.11945701);
		positionsevt.add(positionevt1);
		PositionevtMdl positionevt2 = new PositionevtMdl();
		positionevt2.setEvt("DEBUT_CLIENT:2756");
		positionevt2.setLatitude(48.84181595);
		positionevt2.setLongitude(2.24256420);
		positionsevt.add(positionevt2);
		PositionevtMdl positionevt3 = new PositionevtMdl();
		positionevt3.setEvt("FIN_CLIENT:2756");
		positionevt3.setLatitude(48.76117706);
		positionevt3.setLongitude(2.11945701);
		positionsevt.add(positionevt3);
		PositionevtMdl positionevt8 = new PositionevtMdl();
		positionevt8.setEvt("FIN_TOURNEE");
		positionevt8.setLatitude(48.76117706);
		positionevt8.setLongitude(2.11945701);
		positionsevt.add(positionevt8);
		
		Map<String, Long> mapDistance = tourneeSuiviService.mapDistance(positionsevt);
		
		assertThat(mapDistance.size()).isEqualTo(2);
		assertThat(mapDistance.get("total")).isGreaterThan(0);
	}
	
	@Test
	public void calculDhproximite_ok1() {
		Date dhdebut = Calendar.getInstance().getTime();
		Date dhproximite = null;
		assertThat(tourneeSuiviService.calculDhproximite(dhdebut, dhproximite)).isEqualTo(dhdebut);
	}
	
	@Test
	public void calculDhproximite_ok2() {
		Calendar caldebut = Calendar.getInstance();
		Date dhdebut = caldebut.getTime();
		Calendar calproximite = caldebut;
		calproximite.add(Calendar.MINUTE, 1);
		Date dhproximite = calproximite.getTime();
		assertThat(tourneeSuiviService.calculDhproximite(dhdebut, dhproximite)).isEqualTo(dhdebut);
	}
	
	@Test
	public void calculDhproximite_ok3() {
		Calendar caldebut = Calendar.getInstance();
		Date dhdebut = caldebut.getTime();
		Calendar calproximite = caldebut;
		calproximite.add(Calendar.MINUTE, -1);
		Date dhproximite = calproximite.getTime();
		assertThat(tourneeSuiviService.calculDhproximite(dhdebut, dhproximite)).isEqualTo(dhproximite);
	}
}
