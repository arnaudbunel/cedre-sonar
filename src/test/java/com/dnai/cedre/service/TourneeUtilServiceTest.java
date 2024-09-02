package com.dnai.cedre.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.dnai.cedre.config.ComponentConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.dnai.cedre.CedreApplicationTI;
import com.dnai.cedre.model.ose.DispoMdl;
import com.dnai.cedre.model.ose.OpMdl;
import com.dnai.cedre.model.ose.OperationsMdl;

@ActiveProfiles("test")
@SpringBootTest(classes = {ComponentConfig.class, TourneeUtilServiceTest.TestConfig.class})
public class TourneeUtilServiceTest {
    @Configuration
    static class TestConfig {
  
        @Bean
        public TourneeUtilService tourneeUtilService() {
            return new TourneeUtilService();
        }

		@Bean
		public ProfileUtilService profileUtilService() {
			return Mockito.mock(ProfileUtilService.class);
		}
    }
    
    @Autowired
 	private TourneeUtilService tourneeUtilService;
    
    private static DispoMdl dispo01 = new DispoMdl();
    private static DispoMdl dispo02 = new DispoMdl();
    private static DispoMdl dispo02Bis = new DispoMdl();
    private static DispoMdl dispo03 = new DispoMdl();
    private static DispoMdl dispo04 = new DispoMdl();
    private static DispoMdl dispo05 = new DispoMdl();
    
    private static OpMdl op01 = new OpMdl();
    private static OpMdl op02 = new OpMdl();
    private static OpMdl op02Bis = new OpMdl();
    private static OpMdl op02Ter = new OpMdl();
    private static OpMdl op03 = new OpMdl();
    private static OpMdl op04 = new OpMdl();
    private static OpMdl op05 = new OpMdl();
    private static OpMdl opDispoNull = new OpMdl();
    
    private static OperationsMdl operations01 = new OperationsMdl();
    private static OperationsMdl operations02 = new OperationsMdl();
    private static OperationsMdl operations03 = new OperationsMdl();
    
    @BeforeAll
    public static void setup() {
    	dispo01.setValue("Prendre les palettes à côté du bac papier");
    	dispo02.setColcons("emplacement corbeilles");
    	dispo02Bis.setColcons("emplacement corbeilles bis");
    	dispo02.setNom("BAC 240 L ");
    	dispo02Bis.setNom("BAC 240 L ");
    	dispo03.setNom("Sacs");
    	dispo04.setNom("BAC 120 L fermé ALU");
    	dispo05.setNom("Corbeille marron croisillon");
    	
    	op01.setDispo(Arrays.asList(dispo01));
    	op01.setPrestation("Collecte");
    	op02.setDispo(Arrays.asList(dispo02));
    	op02Bis.setDispo(Arrays.asList(dispo02));
    	op02Ter.setDispo(Arrays.asList(dispo02Bis));
    	op03.setDispo(Arrays.asList(dispo03));
    	op04.setDispo(Arrays.asList(dispo04));
    	op05.setDispo(Arrays.asList(dispo05));
    	
    	List<OpMdl> ops01 = new ArrayList<>();
    	ops01.add(op01);
    	operations01.setOp(ops01);
    	
    	List<OpMdl> ops02 = new ArrayList<>();
    	ops02.add(op01);
    	ops02.add(op03);
    	operations02.setOp(ops02);
    	
    	List<OpMdl> ops03 = new ArrayList<>();
    	ops03.add(op01);
    	ops03.add(op04);
    	operations03.setOp(ops03);
    }
    
	@Test
	public void genereCleOseOperation_ok1() {
		assertThat(tourneeUtilService.genereCleOseOperation(op02,dispo02)).isEqualTo(tourneeUtilService.genereCleOseOperation(op02Bis,dispo02));
	}
	
	@Test
	public void genereCleOseOperation_ok2() {
		assertThat(tourneeUtilService.genereCleOseOperation(op02,dispo02)).isNotEqualTo(tourneeUtilService.genereCleOseOperation(op02Ter,dispo02Bis));
	}
	
	@Test
	public void genereCleOseOperation_ok3() {
		assertThat(tourneeUtilService.genereCleOseOperation(op02,dispo02)).isNotEqualTo(tourneeUtilService.genereCleOseOperation(opDispoNull,null));
	}
    
	@Test
	public void formatConsigne_ok1() {
		assertThat(tourneeUtilService.formatConsigne(dispo01)).isEqualTo("Prendre les palettes à côté du bac papier . ");
	}
	
	@Test
	public void formatConsigne_ok2() {
		assertThat(tourneeUtilService.formatConsigne(dispo02)).isEqualTo("emplacement corbeilles");
	}
	
	@Test
	public void calculInfocovid_ok1() {
		assertThat(tourneeUtilService.calculInfocovid(operations01)).isFalse();
	}
	
	@Test
	public void calculInfocovid_ok2() {
		assertThat(tourneeUtilService.calculInfocovid(operations02)).isTrue();
	}
	
	@Test
	public void calculInfocovid_ok3() {
		assertThat(tourneeUtilService.calculInfocovid(operations03)).isTrue();
	}
	
	@Test
	public void calculCodetype_ok1() {
		assertThat(tourneeUtilService.calculCodetype(op01)).isEqualTo("C");
	}
	
	@Test
	public void calculCodetype_ok2() {
		assertThat(tourneeUtilService.calculCodetype(op02)).isEqualTo("?");
	}
	
	@Test
	public void extraitDateTournee_ok1() {
		String datefmt = "2018-10-15";
		Date dateExtraite = tourneeUtilService.extraitDateTournee(datefmt);
		assertThat(dateExtraite).isNotNull();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateExtraite);
		assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(15);
		assertThat(cal.get(Calendar.MONTH)).isEqualTo(10-1);
	}
	
	@Test
	public void extraitDateTournee_ok2() {
		String datefmt = "2018-11-6";
		Date dateExtraite = tourneeUtilService.extraitDateTournee(datefmt);
		assertThat(dateExtraite).isNotNull();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateExtraite);
		assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(6);
		assertThat(cal.get(Calendar.MONTH)).isEqualTo(11-1);
	}
	
	@Test
	public void extraitDateTournee_ok3() {
		String datefmt = "2018-2-6";
		Date dateExtraite = tourneeUtilService.extraitDateTournee(datefmt);
		assertThat(dateExtraite).isNotNull();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateExtraite);
		assertThat(cal.get(Calendar.DAY_OF_MONTH)).isEqualTo(6);
		assertThat(cal.get(Calendar.MONTH)).isEqualTo(1);
	}
	
	@Test
	public void memeElementAdresse_ok1() {
		String elementAdr1 = "adresse 1";
		String elementAdr2 = "adresse 1";
		assertThat(tourneeUtilService.memeElementAdresse(elementAdr1, elementAdr2)).isTrue();
	}
	
	@Test
	public void memeElementAdresse_ok2() {
		String elementAdr1 = "adresse 1";
		String elementAdr2 = "adresse 2";
		assertThat(tourneeUtilService.memeElementAdresse(elementAdr1, elementAdr2)).isFalse();
	}
	
	@Test
	public void memeElementAdresse_ok3() {
		String elementAdr1 = "adresse 1";
		String elementAdr2 = null;
		assertThat(tourneeUtilService.memeElementAdresse(elementAdr1, elementAdr2)).isFalse();
	}
}
