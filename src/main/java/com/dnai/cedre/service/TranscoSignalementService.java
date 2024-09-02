package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.TranscoSignalementRepository;
import com.dnai.cedre.domain.TranscoSignalement;
import com.dnai.cedre.model.ose.MandantOseMdl;
import com.dnai.cedre.model.ose.SignalementOseMdl;
import com.dnai.cedre.model.ose.SignalementOseRootMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TranscoSignalementService extends ParentService{

	@Autowired
	private Environment env;
	
	@Autowired
	private HttpClientBuilder httpClientBuilder;
	
	@Autowired
	private TranscoSignalementRepository transcoSignalementRepository;
	
	@Transactional
	public void updateTranscoSignalements() {
		try {
			List<MandantOseMdl> signalementsParMandant = signalementsParMandant();
			log.debug("updateTranscoSignalements : signalementsParMandant : {}",signalementsParMandant);
			if(!signalementsParMandant.isEmpty()) {
				int nbsignalementsOse = 0;
				for(MandantOseMdl mandantOseMdl : signalementsParMandant) {
					int idmandant = strToInteger(mandantOseMdl.getId());
					nbsignalementsOse = nbsignalementsOse + mandantOseMdl.getSignalement().size();
					for(SignalementOseMdl signalementOseMdl : mandantOseMdl.getSignalement()) {
						if(!Strings.isBlank(signalementOseMdl.getTitre())) {
						TranscoSignalement transcoSignalement = transcoSignalementRepository.findFirstByIdcedreAndIdmandant(signalementOseMdl.getId(), idmandant);
							if(transcoSignalement==null) {
								transcoSignalement = new TranscoSignalement();
								transcoSignalement.setActif(true);
								transcoSignalement.setIdmandant(idmandant);
								transcoSignalement.setIdcedre(signalementOseMdl.getId());
								transcoSignalement.setTitre(StringUtils.abbreviate(signalementOseMdl.getTitre(),50));
								transcoSignalement.setImageurl(signalementOseMdl.getIllus());
								transcoSignalementRepository.save(transcoSignalement);
							}else {
								if(!transcoSignalement.getTitre().equals(signalementOseMdl.getTitre()) 
										|| ((Strings.isBlank(transcoSignalement.getImageurl()) && !Strings.isBlank(signalementOseMdl.getIllus())) ||
												(!Strings.isBlank(transcoSignalement.getImageurl()) && !transcoSignalement.getImageurl().equals(signalementOseMdl.getIllus()))
												)) {
									transcoSignalement.setTitre(StringUtils.abbreviate(signalementOseMdl.getTitre(),50));
									transcoSignalement.setImageurl(signalementOseMdl.getIllus());
									transcoSignalementRepository.save(transcoSignalement);
								}
							}
						}
					}
				}
				
				long nbsignalements = transcoSignalementRepository.count();
				if(nbsignalements!=nbsignalementsOse) {
					log.warn("updateTranscoSignalements : {} signalements au lieu de {} dans ose",nbsignalements,nbsignalementsOse);
				}
				
			}
		}catch(Exception e) {
			log.error("updateTranscoSignalements",e);
		}
	}
	
	private List<MandantOseMdl> signalementsParMandant(){
		List<MandantOseMdl> signalementsParMandant = new ArrayList<>();
		try {
			String endPoint = env.getProperty(Constantes.OSE_SERVICE_GETSIGNALEMENTS);
			
			CloseableHttpClient httpClient = httpClientBuilder.build();
			HttpGet httpGet = new HttpGet(endPoint);
										
		    HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String fluxose = EntityUtils.toString(entity);
			if (fluxose != null) {			
				Serializer serializer = new Persister();
				SignalementOseRootMdl signalementOseRootMdl = serializer.read(SignalementOseRootMdl.class, fluxose, false);
				signalementsParMandant.addAll(signalementOseRootMdl.getMandant());
			}
		}catch(Exception e) {
			log.error("signalementsParMandant",e);
		}
		return signalementsParMandant;
	}
}
