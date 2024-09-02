package com.dnai.cedre.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.dnai.cedre.config.ComponentConfig;
import com.dnai.cedre.config.HttpClientConfig;
import com.dnai.cedre.dao.UtilisateurRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.dnai.cedre.CedreApplicationTI;
import com.dnai.cedre.model.cockpit.AuthentificationRetourMdl;
import com.dnai.cedre.model.cockpit.CredentialMdl;

@ActiveProfiles("test")
@SpringBootTest(classes = {HttpClientConfig.class, ComponentConfig.class, AuthentificationCockpitServiceTest.TestConfig.class})
public class AuthentificationCockpitServiceTest {
	
    @Configuration
    static class TestConfig {
  
        @Bean
        public AuthentificationCockpitService authentificationCockpitService() {
            return new AuthentificationCockpitService();
        }

		@Bean
		public UtilisateurRepository utilisateurRepository() {
			return Mockito.mock(UtilisateurRepository.class);
		}
    }
	
    @Autowired
	private AuthentificationCockpitService authentificationCockpitService;
    
	@Test
	@Disabled
	public void authentificationCockpit_erreur1() {
		CredentialMdl credentialMdl = new CredentialMdl();
		credentialMdl.setLogin("xx@cedre.info");
		credentialMdl.setPassword("test");
		
		AuthentificationRetourMdl authentificationRetourMdl = authentificationCockpitService.authentificationCockpit(credentialMdl);
		
		assertThat(authentificationRetourMdl).isNotNull();
		assertThat(authentificationRetourMdl.isAuthentifie()).isFalse();
		assertThat(authentificationRetourMdl.getMessage()).isNotBlank();
	}
}
