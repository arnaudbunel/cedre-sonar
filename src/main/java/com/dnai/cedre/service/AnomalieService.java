package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.ClientoseRepository;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.dao.SignalementRepository;
import com.dnai.cedre.dao.TourneeAgentRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.domain.Signalement;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.domain.TourneeAgent;
import com.dnai.cedre.model.cockpit.AnomalieCritereMdl;
import com.dnai.cedre.model.suivi.AnoclientMdl;
import com.dnai.cedre.model.suivi.AnomediaMdl;
import com.dnai.cedre.model.suivi.AnoprestationMdl;
import com.dnai.cedre.model.suivi.TourneeSuiviMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AnomalieService extends ParentService{

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private ClientoseRepository clientoseRepository;
		
	@Autowired
	private TourneeAgentRepository tourneeAgentRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private PrestationRepository prestationRepository;
	
	@Autowired
	private SignalementRepository signalementRepository;
	
	@Transactional
	public List<TourneeSuiviMdl> tourneesuivi(AnomalieCritereMdl anomalieCritereMdl){
		List<TourneeSuiviMdl> tournees = new ArrayList<>();
		
		List<Tournee> listTournees = tourneesParCriteres(anomalieCritereMdl);
		
		for(Tournee tournee : listTournees) {
			if(filtreTournee(tournee, anomalieCritereMdl.getIdcentre())) {
				TourneeSuiviMdl tourneeSuiviMdl = new TourneeSuiviMdl();
				tourneeSuiviMdl.setDatefmt(longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET));
				tourneeSuiviMdl.setEcart(tournee.isEcart());
				tourneeSuiviMdl.setId(tournee.getId());
				tourneeSuiviMdl.setLibequipe(tournee.getLibequipe());
				tourneeSuiviMdl.setLibtournee(contruitLibtournee(tournee));
				tourneeSuiviMdl.setSignalement(tournee.isSignt());
				
				tournees.add(tourneeSuiviMdl);
			}
		}
		
		return tournees;
	}
	
	private String contruitLibtournee(Tournee tournee) {
		return tournee.getIdose() + " - tour " + tournee.getNotour();
	}
	
	@Transactional
	List<Tournee> tourneesParCriteres(AnomalieCritereMdl anomalieCritereMdl){		
		Clientose clientose = null;
		
		List<Tournee> listTourneesTemp = new ArrayList<>();
		// critere client renseigné
		if(anomalieCritereMdl.getIdclient()>0) {
			clientose = clientoseRepository.findById(anomalieCritereMdl.getIdclient()).get();
			if(clientose!=null) {
				List<Collecte> collectes = collecteRepository.findByClientoseAndTourneeDatetourneeBetweenAndTourneeEtat(clientose, parseDate(anomalieCritereMdl.getDhdebut(),Constantes.PATTERN_DATETIRET), parseDate(anomalieCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET), Constantes.TOURNEE_ETAT_FIN);
				for(Collecte collecte : collectes) {
					Tournee tournee = collecte.getTournee();
					if(tournee.isEcart() || tournee.isSignt()) {
						listTourneesTemp.add(tournee);
					}
				}
			}
		}else {
			List<Tournee> listTournee = tourneeRepository.findByDatetourneeBetweenAndEtatAndTypetournee(parseDate(anomalieCritereMdl.getDhdebut(),Constantes.PATTERN_DATETIRET), 
					parseDate(anomalieCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET), Constantes.TOURNEE_ETAT_FIN, Tournee.Typetournee.COLLECTE.toString());
			if(anomalieCritereMdl.getIdutilisateur()>0) {
				Map<Long,Clientose> clientOsePerimetre = clientOseParPerimetre(anomalieCritereMdl.getIdutilisateur(), 0);
				for(Tournee tournee : listTournee) {
					if((tournee.isEcart() || tournee.isSignt()) && contientClientPerimetre(clientOsePerimetre, tournee)) {
						listTourneesTemp.add(tournee);
					}
				}
			}else {
				for(Tournee tournee : listTournee) {
					if(tournee.isEcart() || tournee.isSignt()) {
						listTourneesTemp.add(tournee);
					}
				}
			}
		}
		
		// critere agent
		List<TourneeAgent> tourneeAgents = new ArrayList<>();
		if(anomalieCritereMdl.getIdagent()>0) {
			tourneeAgents = tourneeAgentRepository.findByIdagentose(anomalieCritereMdl.getIdagent());
		}
		
		HashMap<Long,Tournee> mapTournee = new HashMap<>();
		if(!tourneeAgents.isEmpty()) {
			for(Tournee tournee : listTourneesTemp) {
				if(isTourneeInTourneeAgent(tournee, tourneeAgents)) {
					mapTournee.put(tournee.getId(), tournee);
				}
			}
		}else {
			for(Tournee tournee : listTourneesTemp) {
				mapTournee.put(tournee.getId(), tournee);
			}
		}
		
		List<Tournee> listTournees = new ArrayList<>(mapTournee.values());
		
		return listTournees;
	}

	@Transactional
	public List<AnoclientMdl> anomalies(AnomalieCritereMdl anomalieCritereMdl){
		List<AnoclientMdl> anomalies = new ArrayList<>();
		try {
			Tournee tournee = tourneeRepository.findById(anomalieCritereMdl.getIdtournee()).get();
			
			boolean filtreClient = false;
			Clientose clientose = null;
			
			if(anomalieCritereMdl.getIdclient()>0) {
				clientose = clientoseRepository.findById(anomalieCritereMdl.getIdclient()).get();
				if(clientose!=null) {
					filtreClient = true;
				}
			}
			
			List<Collecte> collectes = new ArrayList<>();
			if(filtreClient) {
				collectes = collecteRepository.findByTourneeAndClientose(tournee, clientose);
			}else {
				collectes = collecteRepository.findByTournee(tournee);
			}
			
			Map<Long,Clientose> clientsOsePerimetre = null;
			boolean filtreClientsParPerimetre = false;
			if(anomalieCritereMdl.getIdutilisateur()>0) {
				clientsOsePerimetre = clientOseParPerimetre(anomalieCritereMdl.getIdutilisateur(), 0);
				filtreClientsParPerimetre = true;
			}
			
			for(Collecte collecte : collectes) {
				Clientose clientoseCollecte = collecte.getClientose();

				if(!filtreClientsParPerimetre || (filtreClientsParPerimetre && 
						clientsOsePerimetre.get(clientoseCollecte.getId())!=null)) {
					AnoclientMdl anoclientMdl = new AnoclientMdl();
					boolean isAno = false;
					anoclientMdl.setNom(clientoseCollecte.getNom());
					
					List<Signalement> signalements = signalementRepository.findByCollecte(collecte);
					for(Signalement signalement : signalements) {
						if(signalement.getUrl()!=null || signalement.getTexte()!=null) {
							switch(signalement.getTypesignt()) {
								case "IMAGE":
									AnomediaMdl photo = new AnomediaMdl();
									photo.setUrl(signalement.getUrl());
									anoclientMdl.getPhotos().add(photo);
									break;
								case "AUDIO":
									AnomediaMdl audio = new AnomediaMdl();
									audio.setUrl(signalement.getUrl());
									anoclientMdl.getAudios().add(audio);
									break;
								case "TEXTE":
									anoclientMdl.setAnotexte(signalement.getTexte());
									break;
								default:
									log.error("anomalies : unsupported typesignt {}",signalement.getTypesignt());
							}
							isAno = true;
						}
					}
					
					List<Prestation> prestations = prestationRepository.findByCollecte(collecte);
					for(Prestation prestation : prestations) {
						if(!prestation.getQteprevu().equals(prestation.getQtereel())) {
							AnoprestationMdl anoprestationMdl = new AnoprestationMdl();
							anoprestationMdl.setLibelle(prestation.getLibelle());
							anoprestationMdl.setQteprev(prestation.getQteprevu());
							anoprestationMdl.setQtereel(prestation.getQtereel());
							anoclientMdl.getAnoprestations().add(anoprestationMdl);
							isAno = true;
						}
					}
					if(isAno) {
						anomalies.add(anoclientMdl);	
					}
				}
			}
		}catch(Exception e) {
			log.error("anomalies : " + e.toString());
		}		
		return anomalies;
	}
}
