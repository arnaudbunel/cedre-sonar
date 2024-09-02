package com.dnai.cedre.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.ClientoseRepository;
import com.dnai.cedre.dao.CollecteDibRepository;
import com.dnai.cedre.dao.MediaDibRepository;
import com.dnai.cedre.dao.PrestationDibRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.CollecteDib;
import com.dnai.cedre.domain.MediaDib;
import com.dnai.cedre.domain.PrestationDib;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.cockpit.DibCockpitClientDetailMdl;
import com.dnai.cedre.model.cockpit.DibCockpitClientMdl;
import com.dnai.cedre.model.cockpit.DibPhotoMdl;
import com.dnai.cedre.model.cockpit.PrestationCritereMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DibCockpitClientService extends ParentService{

	@Autowired
	private ClientoseRepository clientoseRepository;
		
	@Autowired
	private CollecteDibRepository collecteDibRepository;
	
	@Autowired
	private PrestationDibRepository prestationDibRepository;
	
	@Autowired
	private MediaDibRepository mediaDibRepository;
	
	@Transactional
	public List<DibCockpitClientMdl> prestations(PrestationCritereMdl prestationCritereMdl){
		List<DibCockpitClientMdl> prestationsMdl = new ArrayList<>();
		
		try {
			Clientose clientose = null;
			if(prestationCritereMdl.getIdclient()>0) {
				clientose = clientoseRepository.findById(prestationCritereMdl.getIdclient()).get();
			}

			List<CollecteDib> collectes = collectesParClientEtDate(clientose, parseDate(prestationCritereMdl.getDhdebut(),Constantes.PATTERN_DATETIRET), parseDate(prestationCritereMdl.getDhfin(),Constantes.PATTERN_DATETIRET), prestationCritereMdl.getEtat());
			
			for(CollecteDib collecte : collectes) {
				Tournee tournee = collecte.getTournee();
				if(Constantes.TOURNEE_ETAT_FIN.equals(tournee.getEtat()) 
						&& filtreTournee(tournee, prestationCritereMdl.getIdcentre())) {
					DibCockpitClientMdl prestationMdl = new DibCockpitClientMdl();
					prestationMdl.setAgents(tournee.getLibequipe());
					prestationMdl.setDatefmt(longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET));
					prestationMdl.setHpassage(collecte.getDhpassage()!=null?longToDateTimeFmtPattern(localDateTimeToMillis(collecte.getDhpassage()),Constantes.PATTERN_HHMMSS):"-");
					prestationMdl.setHpassagetms(collecte.getDhpassage()!=null?localDateTimeToMillis(collecte.getDhpassage()):0);
					prestationMdl.setClient(collecte.getClientose().getNom());
					prestationMdl.setDistance(formatDistance(collecte.getDistance()));
					prestationMdl.setIdtournee(tournee.getId());
					prestationMdl.setEtat(collecte.getEtat());
					
					List<PrestationDib> prestations = prestationDibRepository.findByCollectedib(collecte);
					for(PrestationDib prestation : prestations) {
						DibCockpitClientDetailMdl prestationDetailMdl = new DibCockpitClientDetailMdl();
						prestationDetailMdl.setLibelle(prestation.getLibelle());
						prestationDetailMdl.setQteabsent(String.valueOf(prestation.getQteabsent()));
						prestationDetailMdl.setQteprevue(String.valueOf(prestation.getQteprevu()));
						prestationDetailMdl.setQtereelle(String.valueOf(prestation.getQtereel()));
						prestationDetailMdl.setQtevide(String.valueOf(prestation.getQtevide()));
						prestationDetailMdl.setQtedebord(String.valueOf(prestation.getQtedebord()));
						prestationDetailMdl.setQtedeclassement(String.valueOf(prestation.getQtedeclassement()));
						prestationMdl.getDetails().add(prestationDetailMdl);
					}
					
					List<MediaDib> medias = mediaDibRepository.findByIdcollectedib(collecte.getId());
					
					for(MediaDib media: medias) {
						DibPhotoMdl dibPhotoMdl = new DibPhotoMdl();
						dibPhotoMdl.setDhcreationfmt(longToDateTimeFmtPattern(localDateTimeToMillis(media.getDhcreation()),Constantes.PATTERN_DATEHEURE));
						dibPhotoMdl.setMotif(transcoMotif(media.getMotif()));
						dibPhotoMdl.setUrl(media.getUrl());
						if(media.getIdprestationdib()>0) {
							Optional<PrestationDib> optPrestationDib = prestationDibRepository.findById(media.getIdprestationdib());
							if(optPrestationDib.isPresent()) {
								dibPhotoMdl.setLibellePrestation(optPrestationDib.get().getLibelle());
							}
						}
						prestationMdl.getPhotos().add(dibPhotoMdl);
					}
					
					prestationsMdl.add(prestationMdl);
				}
			}
			Collections.sort(prestationsMdl);
		}catch(Exception e) {
			log.error("prestations : " + e.toString());
		}
		
		return prestationsMdl;
	}
	
	private String transcoMotif(final String motif) {
		String transcoMotif = "";
		
		switch (motif) {
			case "preuve":
				transcoMotif = "preuve de passage";
				break;
			default:
				transcoMotif = motif;
		}
		
		
		return transcoMotif;
	}
	
	private List<CollecteDib> collectesParClientEtDate(Clientose clientose, Date datedebut, Date datefin, String etat){
		List<CollecteDib> listCollectes = new ArrayList<>();
		
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
				listCollectes = collecteDibRepository.findByClientoseAndEtatAndTourneeDatetourneeBetween(clientose,etat,datedebut,datefin);
			}else {
				listCollectes = collecteDibRepository.findByClientoseAndTourneeDatetourneeBetween(clientose,datedebut,datefin);
			}
		} else {
			if(etat!=null) {
				listCollectes = collecteDibRepository.findByEtatAndTourneeDatetourneeBetween(etat,datedebut,datefin);
			}else {
				listCollectes = collecteDibRepository.findByTourneeDatetourneeBetween(datedebut,datefin);
			}
		}
		return listCollectes;
	}
}
