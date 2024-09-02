package com.dnai.cedre.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.CollecteDibRepository;
import com.dnai.cedre.dao.PrestationDibRepository;
import com.dnai.cedre.domain.CollecteDib;
import com.dnai.cedre.domain.PrestationDib;
import com.dnai.cedre.model.MediaMdl;
import com.dnai.cedre.model.dib.ClientdibMdl;
import com.dnai.cedre.model.dib.OperationdibMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CollecteDibService extends ParentService{

	@Autowired
	private CollecteDibRepository collecteDibRepository;
	
	@Autowired
	private PrestationDibRepository prestationDibRepository;
	
	@Autowired
	private MediaService mediaService;
	
	@Transactional
	public void finaliseClient(final ClientdibMdl clientMdl) {
		try {
			log.debug("finaliseClient : clientMdl {}",clientMdl);
			CollecteDib collecte = collecteDibRepository.findById(clientMdl.getIdcollecteadnai()).get();
			if(!Constantes.COLLECTE_ETAT_TRAITE.equals(collecte.getEtat()) 
					&& !Constantes.COLLECTE_ETAT_IMPOSSIBLE.equals(collecte.getEtat())) {
		        LocalDateTime dhpassage =
		        	       LocalDateTime.ofInstant(Instant.ofEpochMilli(clientMdl.getDhpassage()),
		        	                               TimeZone.getDefault().toZoneId());
				collecte.setDhpassage(dhpassage);
				
				collecte.setEtat(clientMdl.getEtat());

				for(OperationdibMdl operationMdl : clientMdl.getOperations()) {
					String libelle = contruitLibPrestation(operationMdl.getLibelle(), operationMdl.getNom());
					PrestationDib prestation = prestationDibRepository.findById(operationMdl.getIdprestationadnai()).orElse(null);
					if(prestation==null) {
						prestation = new PrestationDib();
						prestation.setCollectedib(collecte);
						prestation.setLibelle(libelle);
					}
					prestation.setQteabsent(operationMdl.getQteabsent());
					prestation.setQteprevu(operationMdl.getQteprev());
					prestation.setQtereel(operationMdl.getQtereel());
					prestation.setQtevide(operationMdl.getQtevide());
					prestation.setQtedebord(operationMdl.getQtedebord());
					prestation.setQtedeclassement(operationMdl.getQtedeclassement());
					prestationDibRepository.save(prestation);
				}
				
				for(MediaMdl mediaMdl : clientMdl.getSigltimage()) {
					if(mediaMdl.getBase64media()!=null 
							&& MediaMdl.Etatsync.CLIENT.toString().equals(mediaMdl.getEtatsync())) {
						mediaService.enregistreMediaDib(mediaMdl);
					}
				}
				
				collecteDibRepository.save(collecte);
			}
		}catch(Exception e) {
			log.error("finaliseClient : clientMdl : {}",clientMdl,e);
		}
	}
}
