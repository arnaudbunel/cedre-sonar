package com.dnai.cedre.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.dnai.cedre.config.AmazonTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.dnai.cedre.CedreApplicationTI;
import com.dnai.cedre.model.cockpit.AvpDetailMdl;
import com.dnai.cedre.model.cockpit.AvpMdl;

@ActiveProfiles("test")
@SpringBootTest(classes = {AmazonTestConfig.class, AvisdePassageServiceTest.TestConfig.class})
public class AvisdePassageServiceTest {

    @Configuration
    static class TestConfig {
  
        @Bean
        public AvisdePassageService avisdePassageService() {
            return new AvisdePassageService();
        }
    }
	
    @Autowired
	private AvisdePassageService avisdePassageService;
	
	@Test
	public void genereEtStockeAvp1() {
		
		AvpMdl avpMdl = new AvpMdl();
		avpMdl.setIdavp("TEST-2019-4-1.1.2242");
		avpMdl.setNomClient("ADNAI GFI St ouen");
		avpMdl.setAdresse("1 Rue de la Barbotière");
		avpMdl.setCodepostal("17220");
		avpMdl.setVille("SAINT VIVIEN");
		avpMdl.setIdclientose("TEST-Idclientose");
		avpMdl.setIdtourneeose("TEST-Idtourneeose");
		
		AvpDetailMdl avpDetailMdl1 = new AvpDetailMdl();
		avpDetailMdl1.setOperation("Collecte Papier");
		avpDetailMdl1.setDispositif("BAC 770 L");
		avpDetailMdl1.setQteprev("1");
		avpDetailMdl1.setQtereal("1");
		avpMdl.getDetails().add(avpDetailMdl1);
		
		AvpDetailMdl avpDetailMdl2 = new AvpDetailMdl();
		avpDetailMdl2.setOperation("Collecte Gobelet");
		avpDetailMdl2.setDispositif("BAC 340 L fermé");
		avpDetailMdl2.setQteprev("3");
		avpDetailMdl2.setQtereal("1");
		avpMdl.getDetails().add(avpDetailMdl2);

		AvpDetailMdl avpDetailMdl3 = new AvpDetailMdl();
		avpDetailMdl3.setOperation("Dépôt 6 x Palettes (non fournies)");
		avpDetailMdl3.setDispositif("Palettes (non fournies)");
		avpDetailMdl3.setQteprev("6");
		avpDetailMdl3.setQtereal("");
		avpMdl.getDetails().add(avpDetailMdl3);
		
		AvpDetailMdl avpDetailMdl4 = new AvpDetailMdl();
		avpDetailMdl4.setOperation("Collecte Carton");
		avpDetailMdl4.setDispositif("Balles");
		avpDetailMdl4.setQteprev("2");
		avpDetailMdl4.setQtereal("2");
		avpMdl.getDetails().add(avpDetailMdl4);
		
		avpMdl.setAgents("ADNAI AGENT TEST 01 - ADNAI AGENT TEST 02");
		avpMdl.setDateAvp("14/03/2019");
		avpMdl.setHeureAvp("09:03");
		avpMdl.setVehicule("PL 183 ARG");
		
		avpMdl.setSignature("https://s3.eu-central-1.amazonaws.com/cedre.adn.ai/image/image1539355649863.png");
		avpMdl.setSignaturePresente(true);
		avpMdl.setInfosSignataire("Jacques Dupond - Chef d'atelier");
		
		String urlAvp = avisdePassageService.genereEtStockeAvp(avpMdl);
		assertThat(urlAvp).isNotNull();
	}
	
}
