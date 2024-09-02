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

import com.dnai.cedre.dao.ClientoseRepository;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.cockpit.PrestationCritereMdl;
import com.dnai.cedre.model.cockpit.PrestationDetailMdl;
import com.dnai.cedre.model.cockpit.PrestationMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PrestationService extends ParentService {

	@Autowired
	private ClientoseRepository clientoseRepository;
		
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private PrestationRepository prestationRepository;
	
	@Transactional
	public List<PrestationMdl> prestations(PrestationCritereMdl prestationCritereMdl){
		List<PrestationMdl> prestationsMdl = new ArrayList<>();
		
		try {
			Clientose clientose = null;
			if(prestationCritereMdl.getIdclient()>0) {
				clientose = clientoseRepository.findById(prestationCritereMdl.getIdclient()).get();
			}

			List<Collecte> collectes = collectesParClientEtDate(clientose, parseDate(prestationCritereMdl.getDhdebut(),Constantes.PATTERN_DATETIRET), parseDate(prestationCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET), prestationCritereMdl.getEtat());
			
			for(Collecte collecte : collectes) {
				Tournee tournee = collecte.getTournee();
				if(Constantes.TOURNEE_ETAT_FIN.equals(tournee.getEtat()) 
						&& filtreTournee(tournee, prestationCritereMdl.getIdcentre())) {
					PrestationMdl prestationMdl = new PrestationMdl();
					prestationMdl.setAgents(tournee.getLibequipe());
					prestationMdl.setDatefmt(longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET));
					prestationMdl.setDuree(formatDuree(calculDuree(collecte.getDhdebut(),collecte.getDhfin())));
					prestationMdl.setHdebut(collecte.getDhdebut()!=null?longToDateTimeFmtPattern(collecte.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):"-");
					prestationMdl.setHdebuttms(collecte.getDhdebut()!=null?collecte.getDhdebut().getTime():0);
					prestationMdl.setHfin(collecte.getDhfin()!=null?longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):"-");
					prestationMdl.setPoids(formatPoids(calculPoidsCollecte(collecte).getPoids()));
					prestationMdl.setPoidsparagent(formatPoids(calculPoidsCollecte(collecte).getPoidsparagent()));
					prestationMdl.setHproximite(collecte.getDhproximite()!=null?longToDateTimeFmtPattern(collecte.getDhproximite().getTime(),Constantes.PATTERN_HHMMSS):"-");
					prestationMdl.setClient(collecte.getClientose().getNom());
					prestationMdl.setDistance(formatDistance(collecte.getDistance()));
					prestationMdl.setIdtournee(tournee.getId());
					prestationMdl.setUrlavp(collecte.getUrlavp());
					prestationMdl.setEtat(collecte.getEtat());
					
					List<Prestation> prestations = prestationRepository.findByCollecte(collecte);
					for(Prestation prestation : prestations) {
						PrestationDetailMdl prestationDetailMdl = new PrestationDetailMdl();
						prestationDetailMdl.setLibelle(prestation.getLibelle());
						prestationDetailMdl.setPoids(formatPoids(prestation.getPoids()));
						prestationDetailMdl.setPoidsparagent(formatPoids(prestation.getPoidsparagent()));
						prestationDetailMdl.setQteabsent(String.valueOf(prestation.getQteabsent()));
						prestationDetailMdl.setQteprevue(prestation.getQteprevu());
						prestationDetailMdl.setQtereelle(prestation.getQtereel());
						prestationDetailMdl.setQtevide(String.valueOf(prestation.getQtevide()));
						prestationMdl.getDetails().add(prestationDetailMdl);
					}
					prestationMdl.setEcart(isEcartCollecte(prestations));
					prestationsMdl.add(prestationMdl);
				}
			}
			Collections.sort(prestationsMdl);
		}catch(Exception e) {
			log.error("prestations : " + e.toString());
		}
		
		return prestationsMdl;
	}
	
	private boolean isEcartCollecte(List<Prestation> prestations) {
		boolean isEcartCollecte = false;
		for(Prestation prestation : prestations) {
			if(prestation.getQteabsent()>0) {
				isEcartCollecte = true;
				break;
			}
			int qteprev = parseInt(prestation.getQteprevu());
			int qtereel = parseInt(prestation.getQtereel());
			if(qtereel>qteprev) {
				isEcartCollecte = true;
				break;
			}
		}
		return isEcartCollecte;
	}
	
	private List<Collecte> collectesParClientEtDate(Clientose clientose, Date datedebut, Date datefin, String etat){
		List<Collecte> listCollectes = new ArrayList<>();
		
		LocalDate localDateJour = LocalDate.now();
		
		if(datedebut==null) {
			datedebut = Date.from(localDateJour.atStartOfDay(ZoneId.systemDefault()).toInstant());
			log.error("collectesParClientEtDate : datedebut null, remplacée par date du jour");
		}
		
		if(datefin==null) {
			datefin = datedebut;
		}
		if(clientose!=null) {
			if(etat!=null) {
				listCollectes = collecteRepository.findByClientoseAndEtatAndTourneeDatetourneeBetween(clientose,etat,datedebut,datefin);
			}else {
				listCollectes = collecteRepository.findByClientoseAndTourneeDatetourneeBetween(clientose,datedebut,datefin);
			}
		} else {
			if(etat!=null) {
				listCollectes = collecteRepository.findByEtatAndTourneeDatetourneeBetween(etat,datedebut,datefin);
			}else {
				listCollectes = collecteRepository.findByTourneeDatetourneeBetween(datedebut,datefin);
			}
		}
		return listCollectes;
	}
}
