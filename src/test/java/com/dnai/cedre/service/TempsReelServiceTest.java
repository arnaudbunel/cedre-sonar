package com.dnai.cedre.service;

import com.dnai.cedre.config.AmazonTestConfig;
import com.dnai.cedre.config.ComponentConfig;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.cockpit.TempsReelDetailMdl;
import com.dnai.cedre.util.Constantes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {AmazonTestConfig.class, ComponentConfig.class, TempsReelServiceTest.TestConfig.class})
@Disabled
public class TempsReelServiceTest {

    @Configuration
    static class TestConfig {
        @Bean
        public TempsReelService tempsReelService() {
            return new TempsReelService();
        }

		@Bean
		public CollecteRepository collecteRepository() {
			return Mockito.mock(CollecteRepository.class);
		}
		@Bean
		public TourneeService tourneeService() {
			return Mockito.mock(TourneeService.class);
		}
		@Bean
		public EventTourneeService eventTourneeService() {
			return Mockito.mock(EventTourneeService.class);
		}
    }
	
    @Autowired
	private TempsReelService tempsReelService;
    
    private static Tournee tourneeEnCours = new Tournee();
    private static Tournee tourneeRetourCentre = new Tournee();
    private static Tournee tourneeFin = new Tournee();
    
    private static Collecte collecteAttente = new Collecte();
    private static Collecte collecteEnCours = new Collecte();
    private static Collecte collecteTraite = new Collecte();
    private static Collecte collecteImpossible = new Collecte();
    
    private static TempsReelDetailMdl detailAFaire = new TempsReelDetailMdl();
    private static TempsReelDetailMdl detailSurPlace = new TempsReelDetailMdl();
    private static TempsReelDetailMdl detailEnCours = new TempsReelDetailMdl();
    private static TempsReelDetailMdl detailTraite = new TempsReelDetailMdl();
    private static TempsReelDetailMdl detailImpossible = new TempsReelDetailMdl();

    @BeforeAll
    public static void setup() {
    	Date datejour = new Date();
    	tourneeRetourCentre.setDhcentreretour(datejour);
    	tourneeFin.setDhcentreretour(datejour);
    	tourneeFin.setDhfin(datejour);
    	
    	collecteAttente.setEtat(Constantes.COLLECTE_ETAT_ATTENTE);
    	collecteEnCours.setEtat(Constantes.COLLECTE_ETAT_DEBUT);
    	collecteTraite.setEtat(Constantes.COLLECTE_ETAT_TRAITE);
    	collecteImpossible.setEtat(Constantes.COLLECTE_ETAT_IMPOSSIBLE);
    	
    	detailSurPlace.setEtat(Constantes.TEMPS_REEL_ETAT_SURPLACE);
    	detailEnCours.setEtat(Constantes.TEMPS_REEL_ETAT_ENCOURS);
    	detailAFaire.setEtat(Constantes.TEMPS_REEL_ETAT_AFAIRE);
    	detailTraite.setEtat(Constantes.TEMPS_REEL_ETAT_TRAITE);
    	detailImpossible.setEtat(Constantes.TEMPS_REEL_ETAT_IMPOSSIBLE);
    }
    
	@Test
	public void calculEtatTournee_ok01() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailAFaire,detailAFaire);
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteAttente);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeEnCours, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CLIENT);
	}
	
	@Test
	public void calculEtatTournee_ok02() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailTraite,detailTraite);
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteTraite);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeEnCours, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CENTRE);
	}
	
	@Test
	public void calculEtatTournee_ok03() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailTraite,detailEnCours);
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteEnCours);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeEnCours, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_CLIENT_ENCOURS);
	}
	
	@Test
	public void calculEtatTournee_ok04() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailTraite,detailTraite);
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteTraite);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeRetourCentre, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_PROX_CENTRE);
	}
	
	@Test
	public void calculEtatTournee_ok05() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailTraite,detailTraite);
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteTraite);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeFin, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_TOTAL);
	}
	
	@Test
	public void calculEtatTournee_ok06() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailTraite,detailImpossible);
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteImpossible);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeFin, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_PARTIEL);
	}
	
	@Test
	public void calculEtatTournee_ok07() {
		List<TempsReelDetailMdl> details = Arrays.asList(detailSurPlace,detailAFaire);
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteAttente);
		String etatTournee = tempsReelService.calculEtatTournee(tourneeEnCours, collectes, details);
		assertThat(etatTournee).isEqualTo(Constantes.TEMPS_REEL_ETAT_TOURNEE_CLIENT_PROXIMITE);
	}
	
	@Test
	public void nbClientsRestants_ok1(){
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteTraite);
		int nbClientsRestants = tempsReelService.nbClientsRestants(collectes);
		assertThat(nbClientsRestants).isEqualTo(0);
	}
	
	@Test
	public void nbClientsRestants_ok2(){
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteTraite);
		int nbClientsRestants = tempsReelService.nbClientsRestants(collectes);
		assertThat(nbClientsRestants).isEqualTo(1);
	}
	
	@Test
	public void nbClientsRestants_ok3(){
		List<Collecte> collectes = Arrays.asList(collecteEnCours,collecteTraite);
		int nbClientsRestants = tempsReelService.nbClientsRestants(collectes);
		assertThat(nbClientsRestants).isEqualTo(1);
	}
	
	@Test
	public void nbClientsRestants_ok4(){
		List<Collecte> collectes = Arrays.asList(collecteImpossible,collecteTraite);
		int nbClientsRestants = tempsReelService.nbClientsRestants(collectes);
		assertThat(nbClientsRestants).isEqualTo(0);
	}
	
	@Test
	public void nbClientsRestants_ok5(){
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteAttente);
		int nbClientsRestants = tempsReelService.nbClientsRestants(collectes);
		assertThat(nbClientsRestants).isEqualTo(2);
	}
	
	@Test
	public void nbClientsRestants_ok6(){
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteEnCours);
		int nbClientsRestants = tempsReelService.nbClientsRestants(collectes);
		assertThat(nbClientsRestants).isEqualTo(2);
	}
	
	@Test
	public void isClientEnCours_ok1() {
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteEnCours);
		boolean isClientEnCours = tempsReelService.isClientEnCours(collectes);
		assertThat(isClientEnCours).isTrue();
	}
	
	@Test
	public void isClientEnCours_ok2() {
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteTraite);
		boolean isClientEnCours = tempsReelService.isClientEnCours(collectes);
		assertThat(isClientEnCours).isFalse();
	}
	
	@Test
	public void nbClientsNonTraites_ok1() {
		List<Collecte> collectes = Arrays.asList(collecteAttente,collecteTraite);
		int nbClientsNonTraites = tempsReelService.nbClientsNonTraites(collectes);
		assertThat(nbClientsNonTraites).isEqualTo(1);
	}
	
	@Test
	public void nbClientsNonTraites_ok2() {
		List<Collecte> collectes = Arrays.asList(collecteTraite,collecteTraite);
		int nbClientsNonTraites = tempsReelService.nbClientsNonTraites(collectes);
		assertThat(nbClientsNonTraites).isEqualTo(0);
	}
	
	@Test
	public void nbClientsNonTraites_ok3() {
		List<Collecte> collectes = Arrays.asList(collecteImpossible,collecteTraite);
		int nbClientsNonTraites = tempsReelService.nbClientsNonTraites(collectes);
		assertThat(nbClientsNonTraites).isEqualTo(1);
	}
	
	@Test
	public void isProximiteStricte_ok1(){
		List<TempsReelDetailMdl> details = Arrays.asList(detailAFaire,detailSurPlace);
		boolean isProximiteStricte = tempsReelService.isProximiteStricte(details);
		assertThat(isProximiteStricte).isTrue();
	}
	
	@Test
	public void isProximiteStricte_ok2(){
		List<TempsReelDetailMdl> details = Arrays.asList(detailEnCours,detailSurPlace);
		boolean isProximiteStricte = tempsReelService.isProximiteStricte(details);
		assertThat(isProximiteStricte).isFalse();
	}
}
