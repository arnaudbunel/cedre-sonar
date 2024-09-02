package com.dnai.cedre.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.CollecteDibRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.CollecteDib;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.cockpit.DibTempsReelDetailMdl;
import com.dnai.cedre.model.cockpit.DibTempsReelMdl;
import com.dnai.cedre.model.cockpit.TempsReelDetailMdl;
import com.dnai.cedre.model.cockpit.TempsreelCritereMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DibTempsReelService extends ParentService{

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private CollecteDibRepository collecteRepository;
		
	
	@Transactional
	public List<DibTempsReelMdl> tempsreel(TempsreelCritereMdl tempsreelCritereMdl){
		List<DibTempsReelMdl> listTempsReelMdl = new ArrayList<>();
		
		try {
			LocalDate localDateJour = LocalDate.now().minusDays(5);
			Date dateJour = Date.from(localDateJour.atStartOfDay(ZoneId.systemDefault()).toInstant());
			List<Tournee> tournees = tourneeRepository.findByDatetourneeAfterAndTypetourneeOrderByDhdebut(dateJour, Tournee.Typetournee.DIB.toString());
						
			for(Tournee tournee : tournees) {
				if(filtreTourneeDib(tournee)) {
					DibTempsReelMdl tempsReelMdl = new DibTempsReelMdl();
					tempsReelMdl.setAgents(tournee.getLibequipe());
					tempsReelMdl.setDhdebut(tournee.getDhdebut()!=null?longToDateTimeFmtPattern(tournee.getDhdebut().getTime(),Constantes.PATTERN_DATEHEURE):"-");
					tempsReelMdl.setDhfin(tournee.getDhfin()!=null?longToDateTimeFmtPattern(tournee.getDhfin().getTime(),Constantes.PATTERN_DATEHEURE):"-");
					tempsReelMdl.setIdcentre(tournee.getIdcentre());
					tempsReelMdl.setDhpesee(tournee.getDhpesee()!=null?longToDateTimeFmtPattern(localDateTimeToMillis(tournee.getDhpesee()),Constantes.PATTERN_DATEHEURE):"-");
					tempsReelMdl.setPoidsfmt(tournee.getPoidsdib()!=null?String.valueOf(tournee.getPoidsdib()) + " kg":"");
					tempsReelMdl.setIdtournee(tournee.getId());
					tempsReelMdl.setCodeservice(tournee.getCodeservice());
					
					List<CollecteDib> collectes = collecteRepository.findByTournee(tournee);
					int nbCollectes = 0;
					List<DibTempsReelDetailMdl> details = new ArrayList<>();
					for(CollecteDib collecte : collectes) {
						Clientose clientose = collecte.getClientose();
						DibTempsReelDetailMdl tempsReelDetailMdl = new DibTempsReelDetailMdl();
						tempsReelDetailMdl.setClient(clientose.getNom());
						tempsReelDetailMdl.setEtat(calculEtatTempsReel(collecte));
						tempsReelDetailMdl.setDhpassage(collecte.getDhpassage()!=null?longToDateTimeFmtPattern(localDateTimeToMillis(collecte.getDhpassage()),Constantes.PATTERN_DATEHEURE):"-");
						tempsReelDetailMdl.setDhpassagetms(collecte.getDhpassage()!=null?localDateTimeToMillis(collecte.getDhpassage()):0);
						details.add(tempsReelDetailMdl);
						nbCollectes++;
					}
					
					Collections.sort(details);
					tempsReelMdl.setDetails(details);
					
					String libdetail = "1 à " + nbCollectes;
					tempsReelMdl.setLibdetail(libdetail);
					String etattournee = calculEtatTournee(tournee, collectes, details);
					tempsReelMdl.setEtat(etattournee);
		
					listTempsReelMdl.add(tempsReelMdl);
				}
			}
		}catch(Exception e) {
			log.error("tempsreel : " + e.toString());
		}
		return listTempsReelMdl;
	}
		
	boolean isProximiteStricte(final List<TempsReelDetailMdl> details) {
		int nbProximite = 0;
		int nbEncours = 0;
		for(TempsReelDetailMdl detail : details) {
			if(Constantes.TEMPS_REEL_ETAT_SURPLACE.equals(detail.getEtat())){
				nbProximite ++;
			}else if(Constantes.TEMPS_REEL_ETAT_ENCOURS.equals(detail.getEtat())) {
				nbEncours ++;
			}
		}
		return (nbProximite>0 && nbEncours==0);
	}
	
	String calculEtatTournee(Tournee tournee, List<CollecteDib> collectes, List<DibTempsReelDetailMdl> details) {
		String etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CLIENT;
		try {
			int nbClientsRestants = nbClientsRestants(collectes);
			if(nbClientsRestants==0) {
				if(tournee.getDhpesee()==null) {
					etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_VIDAGE;
				}else {
					if(tournee.getDhfin()==null) {
						etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CENTRE;
					}else {
						int nbClientsNonTraites = nbClientsNonTraites(collectes);
						if(nbClientsNonTraites==0) {
							etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_TOTAL;
						}else {
							etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_PARTIEL;
						}
					}
				}
			}
		}catch(Exception e) {
			log.error("calculEtatTournee : ",e);
		}
		return etatTournee;
	}
		
	boolean isClientEnCours(List<Collecte> collectes) {
		boolean isClientEnCours = false;
		for(Collecte collecte : collectes) {
			if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_DEBUT)) {
				isClientEnCours = true;
				break;
			}
		}
		return isClientEnCours;
	}
	
	int nbClientsNonTraites(List<CollecteDib> collectes) {
		int nbClientsNonTraites = 0;
		for(CollecteDib collecte : collectes) {
			if(!collecte.getEtat().equals(Constantes.COLLECTE_ETAT_TRAITE)) {
				nbClientsNonTraites++;
			}
		}
		return nbClientsNonTraites;
	}
	
	int nbClientsRestants(List<CollecteDib> collectes) {
		int nbClientsRestants = 0;
		for(CollecteDib collecte : collectes) {
			if(!collecte.getEtat().equals(Constantes.COLLECTE_ETAT_TRAITE) && 
					!collecte.getEtat().equals(Constantes.COLLECTE_ETAT_IMPOSSIBLE)) {
				nbClientsRestants++;
			}
		}
		return nbClientsRestants;
	}
	
	
	private String calculEtatTempsReel(CollecteDib collecte) {
		String etat = Constantes.TEMPS_REEL_ETAT_AFAIRE;
		if(collecte.getEtat()!=null) {
			if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_TRAITE)) {
				etat = Constantes.TEMPS_REEL_ETAT_TRAITE;
			}else if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_IMPOSSIBLE)) {
				etat = Constantes.TEMPS_REEL_ETAT_IMPOSSIBLE;
			}
		}
		
		return etat;
	}
}
