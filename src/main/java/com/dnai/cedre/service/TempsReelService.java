package com.dnai.cedre.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.EventTourneeRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.EventTournee;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.EventTourneeMdl;
import com.dnai.cedre.model.cockpit.TempsReelDetailMdl;
import com.dnai.cedre.model.cockpit.TempsReelMdl;
import com.dnai.cedre.model.cockpit.TempsreelCritereMdl;
import com.dnai.cedre.model.cockpit.TourneeFusionCritereMdl;
import com.dnai.cedre.util.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TempsReelService extends ParentService{

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private EventTourneeRepository eventTourneeRepository;
	
	@Autowired
	private AmazonSQS amazonSQS;
	
    @Autowired
    private ObjectMapper objectMapper;
    
	@Autowired
	private Environment env;
	
	public void demandeTourneeFusion(final TourneeFusionCritereMdl tourneeFusionCritereMdl) {
		try {
			String data = objectMapper.writeValueAsString(tourneeFusionCritereMdl);
			EventTournee eventTournee = new EventTournee();
			eventTournee.setDhsaisie(LocalDateTime.now());
			eventTournee.setEvent(Constantes.EVENT_TOURNEE_FUSION);
			eventTournee.setDataevent(data);
			eventTournee.setIdtournee(tourneeFusionCritereMdl.getIdtournee());
			
			long idevent = eventTourneeRepository.save(eventTournee).getId();
			
			EventTourneeMdl eventTourneeMdl = new EventTourneeMdl();
			eventTourneeMdl.setEventtype(Constantes.EVENT_TOURNEE_FUSION);
			eventTourneeMdl.setIdtournee(tourneeFusionCritereMdl.getIdtournee());
			eventTourneeMdl.setIdevent(idevent);
					
			SendMessageRequest sendMessageRequest = new SendMessageRequest(env.getProperty(Constantes.SQS_QUEUE_EVENT_COLLECTE),
					objectMapper.writeValueAsString(eventTourneeMdl));
			amazonSQS.sendMessage(sendMessageRequest);
		}catch(Exception e) {
			log.error("demandeFusionTournee : {}",tourneeFusionCritereMdl,e);
		}
	}
	
	@Transactional
	public List<TempsReelMdl> tempsreel(TempsreelCritereMdl tempsreelCritereMdl){
		List<TempsReelMdl> listTempsReelMdl = new ArrayList<>();
		
		try {
			LocalDate localDateJour = LocalDate.now();
			Date dateJour = Date.from(localDateJour.atStartOfDay(ZoneId.systemDefault()).toInstant());
			//List<Tournee> tournees = tourneeRepository.findByDatetourneeOrderByDhdebut(dateJour);
			List<Tournee> tournees = tourneeRepository.findByDatetourneeAndTypetourneeOrderByDhdebut(dateJour, Tournee.Typetournee.COLLECTE.toString());
			
			Map<Long,Clientose> clientsOsePerimetre = null;
			boolean filtreClientsParPerimetre = false;
			// les utilisateurs sncf ont mandant = -1
			if(tempsreelCritereMdl.getIdutilisateur()>0) {
				clientsOsePerimetre = clientOseParPerimetre(tempsreelCritereMdl.getIdutilisateur(), 0);
				filtreClientsParPerimetre = true;
			}
			
			for(Tournee tournee : tournees) {
				if(filtreTournee(tournee, 0) && (!filtreClientsParPerimetre || 
								(filtreClientsParPerimetre && contientClientPerimetre(clientsOsePerimetre, tournee)))) {
					TempsReelMdl tempsReelMdl = new TempsReelMdl();
					tempsReelMdl.setAgents(tournee.getLibequipe());
					tempsReelMdl.setHdebut(tournee.getDhdebut()!=null?longToDateTimeFmtPattern(tournee.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
					tempsReelMdl.setHfin(tournee.getDhfin()!=null?longToDateTimeFmtPattern(tournee.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
					tempsReelMdl.setIdcentre(tournee.getIdcentre());
					tempsReelMdl.setTour(tournee.getNotour());
					tempsReelMdl.setIdtournee(tournee.getId());
					tempsReelMdl.setCodeservice(tournee.getCodeservice());
					
					List<Collecte> collectes = new ArrayList<>();
					if(tempsreelCritereMdl.getMandant()>0) {
						collectes = collecteRepository.findByTourneeAndMandant(tournee,tempsreelCritereMdl.getMandant());
					}else {
						collectes = collecteRepository.findByTournee(tournee);
					}

					int nbCollectes = 0;
					List<TempsReelDetailMdl> details = new ArrayList<>();
					for(Collecte collecte : collectes) {
						Clientose clientose = collecte.getClientose();
						if(!filtreClientsParPerimetre || (filtreClientsParPerimetre && 
								clientsOsePerimetre.get(clientose.getId())!=null)) {
							TempsReelDetailMdl tempsReelDetailMdl = new TempsReelDetailMdl();
							tempsReelDetailMdl.setClient(clientose.getNom());
							tempsReelDetailMdl.setEtat(calculEtatTempsReel(collecte));
							tempsReelDetailMdl.setHproximite(collecte.getDhproximite()!=null?longToDateTimeFmtPattern(collecte.getDhproximite().getTime(),Constantes.PATTERN_HHMMSS):"-");
							tempsReelDetailMdl.setHdebut(collecte.getDhdebut()!=null?longToDateTimeFmtPattern(collecte.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
							tempsReelDetailMdl.setHfin(collecte.getDhfin()!=null?longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
							tempsReelDetailMdl.setHdebuttms(collecte.getDhdebut()!=null?collecte.getDhdebut().getTime():0);
							details.add(tempsReelDetailMdl);
							nbCollectes++;
						}
					}
					
					Collections.sort(details);
					tempsReelMdl.setDetails(details);
					
					String libdetail = "1 à " + nbCollectes;
					tempsReelMdl.setLibdetail(libdetail);
					String etattournee = calculEtatTournee(tournee, collectes, details);
					tempsReelMdl.setEtat(etattournee);
					tempsReelMdl.setAvecdefaut(avecDefaut(collectes));
					boolean fusionPossible = calculFusionPossible(tournee.getNombretour(), etattournee);
					tempsReelMdl.setFusionpossible(fusionPossible);
					if(fusionPossible) {
						tempsReelMdl.setTourpourfusion(tempsReelMdl.getTour()+1);
					}
					if(tempsreelCritereMdl.getMandant()==0 || (tempsreelCritereMdl.getMandant()>0 && nbCollectes>0)) {
						listTempsReelMdl.add(tempsReelMdl);
					}
				}
			}
		}catch(Exception e) {
			log.error("tempsreel : " + e.toString());
		}
		return listTempsReelMdl;
	}
	
	boolean calculFusionPossible(final int nbtour, final String etat) {
		boolean calculFusionPossible = false;
		if(nbtour>1 && !Constantes.TEMPS_REEL_ETAT_TOURNEE_PROX_CENTRE.equals(etat)
				/*&& !Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CENTRE.equals(etat)
				&& !Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_TOTAL.equals(etat)
				&& !Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_PARTIEL.equals(etat)*/) {
			calculFusionPossible = true;
		}
		return calculFusionPossible;
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
	
	String calculEtatTournee(Tournee tournee, List<Collecte> collectes, List<TempsReelDetailMdl> details) {
		String etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CLIENT;
		try {
			if(isProximiteStricte(details)) {
				etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_CLIENT_PROXIMITE;
			}else {
				if(tournee.getDhcentreretour()!=null && tournee.getDhfin()==null) {
					etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_PROX_CENTRE;
				}else if((tournee.getDhcentreretour()!=null && tournee.getDhfin()!=null) || (
						tournee.getDhcentreretour()==null && tournee.getDhfin()!=null)) {
					int nbClientsNonTraites = nbClientsNonTraites(collectes);
					if(nbClientsNonTraites==0) {
						etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_TOTAL;
					}else {
						etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_TRAITE_PARTIEL;
					}
				}else{
					int nbClientsRestants = nbClientsRestants(collectes);
					if(nbClientsRestants==0) {
						etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_VERS_CENTRE;
					}else {
						if(isClientEnCours(collectes)) {
							etatTournee = Constantes.TEMPS_REEL_ETAT_TOURNEE_CLIENT_ENCOURS;
						}
					}
				}
			}
		}catch(Exception e) {
			log.error("calculEtatTournee : " + e.toString());
		}
		return etatTournee;
	}
	
	private boolean avecDefaut(List<Collecte> collectes) {
		boolean avecDefaut = false;
		for(Collecte collecte : collectes) {
			if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_IMPOSSIBLE)) {
				avecDefaut = true;
				break;
			}
		}
		return avecDefaut;
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
	
	int nbClientsNonTraites(List<Collecte> collectes) {
		int nbClientsNonTraites = 0;
		for(Collecte collecte : collectes) {
			if(!collecte.getEtat().equals(Constantes.COLLECTE_ETAT_TRAITE)) {
				nbClientsNonTraites++;
			}
		}
		return nbClientsNonTraites;
	}
	
	int nbClientsRestants(List<Collecte> collectes) {
		int nbClientsRestants = 0;
		for(Collecte collecte : collectes) {
			if(!collecte.getEtat().equals(Constantes.COLLECTE_ETAT_TRAITE) && 
					!collecte.getEtat().equals(Constantes.COLLECTE_ETAT_IMPOSSIBLE)) {
				nbClientsRestants++;
			}
		}
		return nbClientsRestants;
	}
	
	
	private String calculEtatTempsReel(Collecte collecte) {
		String etat = Constantes.TEMPS_REEL_ETAT_AFAIRE;
		if(collecte.getEtat()!=null) {
			if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_TRAITE)) {
				etat = Constantes.TEMPS_REEL_ETAT_TRAITE;
			}else if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_IMPOSSIBLE)) {
				etat = Constantes.TEMPS_REEL_ETAT_IMPOSSIBLE;
			}else if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_DEBUT)) {
				etat = Constantes.TEMPS_REEL_ETAT_ENCOURS;
			}else if(collecte.getEtat().equals(Constantes.COLLECTE_ETAT_ATTENTE)) {
				if(collecte.getDhproximite()!=null) {
					etat = Constantes.TEMPS_REEL_ETAT_SURPLACE;
				}
			}
		}else {
			if(collecte.getDhproximite()!=null) {
				etat = Constantes.TEMPS_REEL_ETAT_SURPLACE;
			}
		}
		
		return etat;
	}
}
