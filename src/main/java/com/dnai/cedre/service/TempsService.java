package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.AgentoseRepository;
import com.dnai.cedre.dao.CentreRepository;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.PositionevtRepository;
import com.dnai.cedre.dao.TourneeAgentRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Agentose;
import com.dnai.cedre.domain.Centre;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Positionevt;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.domain.TourneeAgent;
import com.dnai.cedre.model.cockpit.CentreTourneeMdl;
import com.dnai.cedre.model.cockpit.ComparatorTourneeDate;
import com.dnai.cedre.model.cockpit.InfosClientMdl;
import com.dnai.cedre.model.cockpit.TempsClientMdl;
import com.dnai.cedre.model.cockpit.TempsCritereMdl;
import com.dnai.cedre.model.cockpit.TempsInfoMdl;
import com.dnai.cedre.model.cockpit.TempsMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TempsService extends ParentService{
	
	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private TourneeAgentRepository tourneeAgentRepository;
	
	@Autowired
	private AgentoseRepository agentoseRepository;
	
	@Autowired
	private PositionevtRepository positionevtRepository;
	
	@Autowired
	private CentreRepository centreRepository;
	
	@Transactional
	public TempsInfoMdl temps(TempsCritereMdl tempsCritereMdl){
		TempsInfoMdl tempsInfoMdl = new TempsInfoMdl();
		try {
			List<Tournee> listTournees = tourneesParCriteres(tempsCritereMdl);
			String agent = "-";
			if(tempsCritereMdl.getIdagent()>0) {
				Agentose agentose = agentoseRepository.findById(tempsCritereMdl.getIdagent()).get();
				if(agentose!=null) {
					agent = agentose.getNom();
				}
			}
			tempsInfoMdl.setAgent(agent);
			
			long dureeTotale = 0;
			double poidsTotal = 0;
			int nbTournee = listTournees.size();
			
			List<TempsMdl> tempsMdl = new ArrayList<>();
			
			for(Tournee tournee : listTournees) {
				TempsMdl tpsMdl = new TempsMdl();
				
				tpsMdl.setAgent(agent);
				tpsMdl.setDatefmt(longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET));

				String distanceFmt = formatDistance(tournee.getDistance());				
				tpsMdl.setDistance(distanceFmt);
				long duree = calculDuree(tournee.getDhdebut(),tournee.getDhfin());
				dureeTotale = dureeTotale + duree;
				tpsMdl.setDuree(formatDuree(duree));
				tpsMdl.setHdebut(tournee.getDhdebut()!=null?longToDateTimeFmtPattern(tournee.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
				tpsMdl.setHdebuttms(tournee.getDhdebut()!=null?tournee.getDhdebut().getTime():0);
				tpsMdl.setHfin(tournee.getDhfin()!=null?longToDateTimeFmtPattern(tournee.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
				tpsMdl.setHdepartcentre(tournee.getDhcentredepart()!=null?longToDateTimeFmtPattern(tournee.getDhcentredepart().getTime(),Constantes.PATTERN_HHMMSS):"-");
				tpsMdl.setHretourcentre(tournee.getDhcentreretour()!=null?longToDateTimeFmtPattern(tournee.getDhcentreretour().getTime(),Constantes.PATTERN_HHMMSS):"-");

				
				List<Collecte> collectes = collecteRepository.findByTourneeOrderByDhdebut(tournee);
				String libdetail = "1 à " + collectes.size();
				tpsMdl.setLibdetail(libdetail);
				
				double poidsTournee = 0;
				Date debutTrajet = tournee.getDhdebut();
				
				for(Collecte collecte : collectes) {
					double poidsCollecte = calculPoidsCollecte(collecte).getPoidsparagent();
					poidsTournee = poidsTournee + poidsCollecte;
					TempsClientMdl tempsClientMdl = new TempsClientMdl();
					Clientose clientose = collecte.getClientose();
					tempsClientMdl.setPoids(formatPoids(poidsCollecte));
					tempsClientMdl.setDuree(formatDuree(calculDuree(collecte.getDhdebut(),collecte.getDhfin())));
					tempsClientMdl.setHdebut(collecte.getDhdebut()!=null?longToDateTimeFmtPattern(collecte.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
					tempsClientMdl.setHfin(collecte.getDhfin()!=null?longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
					tempsClientMdl.setHproximite(collecte.getDhproximite()!=null?longToDateTimeFmtPattern(collecte.getDhproximite().getTime(),Constantes.PATTERN_HHMMSS):"-");
					tempsClientMdl.setTempstrajet(formatDuree(calculDuree(debutTrajet,collecte.getDhproximite())));
					debutTrajet = collecte.getDhfin();
					
					InfosClientMdl infosClientMdl = new InfosClientMdl();
					infosClientMdl.setLongitude(clientose.getLongitude());
					infosClientMdl.setLatitude(clientose.getLatitude());
					infosClientMdl.setNom(clientose.getNom());
					infosClientMdl.setAdr1(clientose.getAdr1());
					infosClientMdl.setAdr2(clientose.getAdr2());
					infosClientMdl.setCodepostal(clientose.getCodepostal());
					infosClientMdl.setVille(clientose.getVille());
					
					tempsClientMdl.setInfosclient(infosClientMdl);
					tempsClientMdl.setEtat(collecte.getEtat());
					
					tpsMdl.getTempsclients().add(tempsClientMdl);
				}
				tpsMdl.setPoids(formatPoids(poidsTournee));
				poidsTotal = poidsTotal + poidsTournee;
				
				
				CentreTourneeMdl centreTourneeMdl = null;
				Centre centre = centreRepository.findById(Integer.valueOf(tournee.getIdcentre()).longValue()).get();
				if(centre!=null) {
					centreTourneeMdl = new CentreTourneeMdl(centre.getLatitude(),centre.getLongitude());
				}else {
					Positionevt positionevt = positionevtRepository.findFirstByTourneeAndEvt(tournee, "DEBUT_TOURNEE");
					if(positionevt!=null) {
						centreTourneeMdl = new CentreTourneeMdl(positionevt.getLatitude(),positionevt.getLongitude());
					}
				}
				tpsMdl.setCentretournee(centreTourneeMdl);
		
				tempsMdl.add(tpsMdl);
			}
			tempsInfoMdl.setDureeMoyenneTournee(nbTournee>0?formatDuree(dureeTotale/nbTournee):"-");
			tempsInfoMdl.setPoidsMoyen(nbTournee>0?formatPoids(poidsTotal/nbTournee):"-");
			tempsInfoMdl.setTemps(tempsMdl);

			Collections.sort(tempsMdl);
		} catch(Exception e) {
			log.error("temps : " + e.toString());
		}
		return tempsInfoMdl;
	}
	
	@Transactional
	private List<Tournee> tourneesParCriteres(TempsCritereMdl tempsCritereMdl){		
		
		List<Tournee> listTourneesTemp = tourneeRepository.findByDatetourneeBetweenAndEtatAndTypetournee(parseDate(tempsCritereMdl.getDhdebut(),"yyyy-MM-dd"), 
					parseDate(tempsCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET), 
					Constantes.TOURNEE_ETAT_FIN, Tournee.Typetournee.COLLECTE.toString());
		List<Tournee> listTourneesFiltre = new ArrayList<>();
		
		for(Tournee tournee : listTourneesTemp) {
			if(filtreTournee(tournee, tempsCritereMdl.getIdcentre())) {
				listTourneesFiltre.add(tournee);
			}
		}
		
		// critere agent
		List<TourneeAgent> tourneeAgents = new ArrayList<>();
		if(tempsCritereMdl.getIdagent()>0) {
			tourneeAgents = tourneeAgentRepository.findByIdagentose(tempsCritereMdl.getIdagent());
		}
		
		HashMap<Long,Tournee> mapTournee = new HashMap<>();
		if(!tourneeAgents.isEmpty()) {
			for(Tournee tournee : listTourneesFiltre) {
				if(isTourneeInTourneeAgent(tournee, tourneeAgents)) {
					mapTournee.put(tournee.getId(), tournee);
				}
			}
		}else {
			for(Tournee tournee : listTourneesFiltre) {
				mapTournee.put(tournee.getId(), tournee);
			}
		}
		
		List<Tournee> listTournees = new ArrayList<>(mapTournee.values());
		Collections.sort(listTournees, new ComparatorTourneeDate());
		
		return listTournees;
	}
}
