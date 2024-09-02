package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.PositionevtRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Positionevt;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.cockpit.CentreTourneeMdl;
import com.dnai.cedre.model.cockpit.Donneepoids;
import com.dnai.cedre.model.cockpit.InfosClientMdl;
import com.dnai.cedre.model.cockpit.TrajetClientMdl;
import com.dnai.cedre.model.cockpit.TrajetCritereMdl;
import com.dnai.cedre.model.cockpit.TrajetMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TrajetService extends ParentService{

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;

	@Autowired
	private PositionevtRepository positionevtRepository;
	
	@Transactional
	public List<TrajetMdl> trajets(TrajetCritereMdl trajetCritereMdl){
		List<TrajetMdl> trajetsMdl = new ArrayList<>();
		
		try {
			List<Tournee> tournees = new ArrayList<>();
			if(trajetCritereMdl.getIdtournee()>0) {
				Tournee tournee = tourneeRepository.findById(trajetCritereMdl.getIdtournee()).get();
				tournees.add(tournee);
			}else {
				if(trajetCritereMdl.getEquipage()!=null) {
					tournees = tourneeRepository.findByLibequipeIgnoreCaseAndDatetourneeBetweenAndEtatAndTypetournee(trajetCritereMdl.getEquipage(), 
							parseDate(trajetCritereMdl.getDhdebut(),Constantes.PATTERN_DATETIRET),
							parseDate(trajetCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET),Constantes.TOURNEE_ETAT_FIN, Tournee.Typetournee.COLLECTE.toString());
				}else {
					tournees = tourneeRepository.findByDatetourneeBetweenAndEtatAndTypetournee(parseDate(trajetCritereMdl.getDhdebut(),Constantes.PATTERN_DATETIRET),
							parseDate(trajetCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET),Constantes.TOURNEE_ETAT_FIN, Tournee.Typetournee.COLLECTE.toString());
				}
			}

			Map<Long,Clientose> clientsOsePerimetre = null;
			boolean filtreClientsParPerimetre = false;
			if(trajetCritereMdl.getIdutilisateur()>0) {
				clientsOsePerimetre = clientOseParPerimetre(trajetCritereMdl.getIdutilisateur(), 0);
				filtreClientsParPerimetre = true;
			}
			
			for(Tournee tournee : tournees) {
				if(filtreTournee(tournee, trajetCritereMdl.getIdcentre()) && 
						(!filtreClientsParPerimetre || 
								(filtreClientsParPerimetre && contientClientPerimetre(clientsOsePerimetre, tournee)))) {
					TrajetMdl trajetMdl = new TrajetMdl();
					
					trajetMdl.setAgents(tournee.getLibequipe());
					trajetMdl.setDatefmt(longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET));
	
					String distanceFmt = formatDistance(tournee.getDistance());				
					trajetMdl.setDistance(distanceFmt);
					trajetMdl.setDuree(formatDuree(calculDuree(tournee.getDhdebut(),tournee.getDhfin())));
					trajetMdl.setHdebut(tournee.getDhdebut()!=null?longToDateTimeFmtPattern(tournee.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
					trajetMdl.setHdebuttms(tournee.getDhdebut()!=null?tournee.getDhdebut().getTime():0);
					trajetMdl.setHfin(tournee.getDhfin()!=null?longToDateTimeFmtPattern(tournee.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
					trajetMdl.setHdepartcentre(tournee.getDhcentredepart()!=null?longToDateTimeFmtPattern(tournee.getDhcentredepart().getTime(),Constantes.PATTERN_HHMMSS):"-");
					trajetMdl.setHretourcentre(tournee.getDhcentreretour()!=null?longToDateTimeFmtPattern(tournee.getDhcentreretour().getTime(),Constantes.PATTERN_HHMMSS):"-");
					
					List<Collecte> collectes = collecteRepository.findByTourneeOrderByDhdebut(tournee);
					
					double poidsTournee = 0;
					double poidsTourneeParAgent = 0;
					int nbCollectes = 0;
					for(Collecte collecte : collectes) {
						Clientose clientose = collecte.getClientose();
						if(!filtreClientsParPerimetre || (filtreClientsParPerimetre && 
								clientsOsePerimetre.get(clientose.getId())!=null)) {
							Donneepoids poids = calculPoidsCollecte(collecte);
							poidsTournee = poidsTournee + poids.getPoids();
							poidsTourneeParAgent = poidsTourneeParAgent + poids.getPoidsparagent();
							TrajetClientMdl trajetClientMdl = new TrajetClientMdl();
							trajetClientMdl.setDistance(formatDistance(collecte.getDistance()));
							trajetClientMdl.setDuree(formatDuree(calculDuree(collecte.getDhdebut(),collecte.getDhfin())));
							trajetClientMdl.setHdebut(collecte.getDhdebut()!=null?longToDateTimeFmtPattern(collecte.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
							trajetClientMdl.setHfin(collecte.getDhfin()!=null?longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
							trajetClientMdl.setHproximite(collecte.getDhproximite()!=null?longToDateTimeFmtPattern(collecte.getDhproximite().getTime(),Constantes.PATTERN_HHMMSS):"-");
							trajetClientMdl.setDureeattente(formatDuree(calculDuree(collecte.getDhproximite(),collecte.getDhdebut())));
							
							InfosClientMdl infosClientMdl = new InfosClientMdl();
							infosClientMdl.setLongitude(clientose.getLongitude());
							infosClientMdl.setLatitude(clientose.getLatitude());
							infosClientMdl.setNom(clientose.getNom());
							infosClientMdl.setAdr1(clientose.getAdr1());
							infosClientMdl.setAdr2(clientose.getAdr2());
							infosClientMdl.setCodepostal(clientose.getCodepostal());
							infosClientMdl.setVille(clientose.getVille());
							
							trajetClientMdl.setInfosclient(infosClientMdl);
							trajetClientMdl.setPoids(formatPoids(poids.getPoids()));
							trajetClientMdl.setPoidsparagent(formatPoids(poids.getPoidsparagent()));
							trajetClientMdl.setEtat(collecte.getEtat());
							
							trajetMdl.getTrajetclients().add(trajetClientMdl);
							nbCollectes++;
						}
					}
					
					String libdetail = "1 à " + nbCollectes;
					trajetMdl.setLibdetail(libdetail);
					trajetMdl.setPoids(formatPoids(poidsTournee));
					trajetMdl.setPoidsparagent(formatPoids(poidsTourneeParAgent));
					
					// TODO à remplacer par le centre de la tournée
					Positionevt positionevt = positionevtRepository.findFirstByTourneeAndEvt(tournee, "DEBUT_TOURNEE");
					if(positionevt!=null) {
						CentreTourneeMdl centreTourneeMdl = new CentreTourneeMdl(positionevt.getLatitude(),positionevt.getLongitude());
						trajetMdl.setCentretournee(centreTourneeMdl);
					}
					
					trajetsMdl.add(trajetMdl);
				}
			}
			
			Collections.sort(trajetsMdl);
		}catch(Exception e) {
			log.error("trajets : " + e.toString());
		}
		
		return trajetsMdl;
	}
}
