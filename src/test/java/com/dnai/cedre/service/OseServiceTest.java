package com.dnai.cedre.service;

import com.dnai.cedre.config.AmazonTestConfig;
import com.dnai.cedre.config.ComponentConfig;
import com.dnai.cedre.config.HttpClientConfig;
import com.dnai.cedre.dao.*;
import com.dnai.cedre.model.AdresseMdl;
import com.dnai.cedre.model.ose.AMdl;
import com.dnai.cedre.model.ose.LocMdl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {AmazonTestConfig.class, HttpClientConfig.class, ComponentConfig.class, OseServiceTest.TestConfig.class})
public class OseServiceTest {
	
    @Configuration
    static class TestConfig {
  
        @Bean
        public OseService oseService() {
            return new OseService();
        }

		@Bean
		public PrestationRepository prestationRepository() {
			return Mockito.mock(PrestationRepository.class);
		}
		@Bean
		public PrestationDibRepository prestationDibRepository() {
			return Mockito.mock(PrestationDibRepository.class);
		}
		@Bean
		public TourneeRepository tourneeRepository() {
			return Mockito.mock(TourneeRepository.class);
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
		public CollecteDibRepository collecteDibRepository() {
			return Mockito.mock(CollecteDibRepository.class);
		}
		@Bean
		public TourneeUtilService tourneeUtilService() {
			return Mockito.mock(TourneeUtilService.class);
		}
		@Bean
		public ProfileUtilService profileUtilService() {
			return Mockito.mock(ProfileUtilService.class);
		}
		@Bean
		public GeolocalisationService geolocalisationService() {
			return Mockito.mock(GeolocalisationService.class);
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
		public MediaRepository mediaRepository() {
			return Mockito.mock(MediaRepository.class);
		}
		@Bean
		public MediaDibRepository mediaDibRepository() {
			return Mockito.mock(MediaDibRepository.class);
		}
		@Bean
		public HistoriqueCodeOseService historiqueCodeOseService() {
			return Mockito.mock(HistoriqueCodeOseService.class);
		}
		@Bean
		public HistoriqueCodeOseRepository historiqueCodeOseRepository() {
			return Mockito.mock(HistoriqueCodeOseRepository.class);
		}
		@Bean
		public TranscoSignalementRepository transcoSignalementRepository() {
			return Mockito.mock(TranscoSignalementRepository.class);
		}
		@Bean
		public DeroulementService deroulementService() {
			return Mockito.mock(DeroulementService.class);
		}
    }
    
    @Autowired
	private OseService oseService;
	
	@Test
	public void construitAdresse_ok1() {
		/*
        <a nom="adr1">1 Boulevard Pasteur</a>
        <a nom="adr2"/>
        <a nom="cp">75015</a>
        <a nom="ville">Paris</a>*/
		LocMdl loc = construitLoc("1 Boulevard Pasteur",null,"75015","Paris");
		AdresseMdl adresseMdl = oseService.construitAdresse(loc);
		assertThat(adresseMdl).isNotNull();
		assertThat(adresseMdl.getAdr2()).isNull();
	}
	
	@Test
	public void construitAdresse_ok2() {
		/*
              <a nom="adr1">238 rue de Vaugirard</a>
               <a nom="adr2">.</a>
               <a nom="cp">75015</a>
               <a nom="ville">PARIS</a>*/
		LocMdl loc = construitLoc("238 rue de Vaugirard",".","75015","Paris");
		AdresseMdl adresseMdl = oseService.construitAdresse(loc);
		assertThat(adresseMdl).isNotNull();
		assertThat(adresseMdl.getAdr2()).isNotNull();
		assertThat(adresseMdl.getLibadr()).isEqualTo("238 rue de Vaugirard,75015 Paris, FR");
	}
	
	@Test
	public void construitAdresse_ok3() {
		/*
              <a nom="adr1">Immeuble Print</a>
               <a nom="adr2">6/8 rue Firmin Gillot</a>
               <a nom="cp">75015</a>
               <a nom="ville">Paris</a>*/
		LocMdl loc = construitLoc("Immeuble Print","6/8 rue Firmin Gillot","75015","Paris");
		AdresseMdl adresseMdl = oseService.construitAdresse(loc);
		assertThat(adresseMdl).isNotNull();
		assertThat(adresseMdl.getAdr2()).isNotNull();
		assertThat(adresseMdl.getLibadr()).isEqualTo("Immeuble Print,6/8 rue Firmin Gillot,75015 Paris, FR");
	}
	
	@Test
	public void construitAdresse_ok4() {
		/*
              <a nom="adr1">238 rue de Vaugirard</a>
               <a nom="adr2">  </a>
               <a nom="cp">75015</a>
               <a nom="ville">PARIS</a>*/
		LocMdl loc = construitLoc("238 rue de Vaugirard","  ","75015","Paris");
		AdresseMdl adresseMdl = oseService.construitAdresse(loc);
		assertThat(adresseMdl).isNotNull();
		assertThat(adresseMdl.getAdr2()).isNotNull();
		assertThat(adresseMdl.getLibadr()).isEqualTo("238 rue de Vaugirard,75015 Paris, FR");
	}
	
	private LocMdl construitLoc(String adr1, String adr2, String cp, String ville) {
		LocMdl loc = new LocMdl();
		List<AMdl> a = new ArrayList<>();
		if(adr1!=null) {
			AMdl aadr1 = new AMdl();
			aadr1.setNom("adr1");
			aadr1.setValue(adr1);
			a.add(aadr1);
		}
		if(adr2!=null) {
			AMdl aadr2 = new AMdl();
			aadr2.setNom("adr2");
			aadr2.setValue(adr2);
			a.add(aadr2);
		}
		if(cp!=null) {
			AMdl acp = new AMdl();
			acp.setNom("cp");
			acp.setValue(cp);
			a.add(acp);
		}
		if(ville!=null) {
			AMdl aville = new AMdl();
			aville.setNom("ville");
			aville.setValue(ville);
			a.add(aville);
		}
		loc.setA(a);
		return loc;
	}
}
