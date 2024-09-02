package com.dnai.cedre.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.dnai.cedre.dao.HistoriqueTourneeRepository;
import com.dnai.cedre.domain.HistoriqueTournee;
import com.dnai.cedre.model.ClientMdl;
import com.dnai.cedre.model.OperationMdl;
import com.dnai.cedre.model.ServiceInfoMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.util.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HistoriqueTourneeService {

	@Autowired
	private HistoriqueTourneeRepository historiqueTourneeRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Transactional
	public ServiceInfoMdl detecteChangements(final ServiceInfoMdl serviceInfoMdl,final String codeserviceSaisi) {
		ServiceInfoMdl serviceInfoMdlCheck = serviceInfoMdl;
		try {
			String codeservice = codeserviceSaisi.toLowerCase().trim();
			String sid = null;
			if(!serviceInfoMdl.getTournees().isEmpty()) {
				sid = serviceInfoMdl.getTournees().get(0).getIdentifiant();
			}
			LocalDateTime ldtSeuil = LocalDateTime.now().minusDays(5);
			HistoriqueTournee historiqueTournee = historiqueTourneeRepository.findFirstByCodeserviceAndIdoseAndDhcreationAfterOrderByDhcreationDesc(codeservice, sid, ldtSeuil);
			if(historiqueTournee==null) {
				historiqueTournee = new HistoriqueTournee();
				historiqueTournee.setCodeservice(codeservice);
				historiqueTournee.setIdose(sid);
				String url = stockeServiceInfo(serviceInfoMdlCheck, codeservice, sid);
				historiqueTournee.setUrlfluxjson(url);
				historiqueTourneeRepository.save(historiqueTournee);
			}else if(historiqueTournee.getUrlfluxjson()!=null){
				String fluxjson = null;
				/*try(BufferedInputStream in = new BufferedInputStream(new URL(historiqueTournee.getUrlfluxjson()).openStream())){
					fluxjson = IOUtils.toString(in, StandardCharsets.UTF_8.name());
				}*/
				String fluxjsonkey = "svcinfo/" + historiqueTournee.getUrlfluxjson().substring(historiqueTournee.getUrlfluxjson().lastIndexOf("/")+1);

				S3Object s3Object = amazonS3.getObject(Constantes.S3_BUCKET, fluxjsonkey);
				S3ObjectInputStream s3is = s3Object.getObjectContent();

				fluxjson = IOUtils.toString(s3is, StandardCharsets.UTF_8.name());
				s3is.close();

				ServiceInfoMdl serviceInfoMdlOriginal = objectMapper.readValue(fluxjson, ServiceInfoMdl.class);
				Map<String,ClientMdl> mapClientMdl = mapClientMdl(serviceInfoMdlOriginal);
				
				String url = stockeServiceInfo(serviceInfoMdlCheck, codeservice, sid);
				historiqueTournee.setUrlfluxjson(url);
				historiqueTournee.setDhupdate(LocalDateTime.now());
				historiqueTourneeRepository.save(historiqueTournee);
				
				// détection ajouts et modifications
				for(TourneeInfoMdl tourneeInfoMdl : serviceInfoMdlCheck.getTournees()) {
					int nbchgtsTournee = 0;
					for(ClientMdl clientMdl : tourneeInfoMdl.getClients()) {
						String cle = clientMdl.getTour() + "-" + clientMdl.getId();
						ClientMdl clientMdlOriginal = mapClientMdl.get(cle);
						if(clientMdlOriginal==null) {
							nbchgtsTournee ++;
							clientMdl.setNbchgts(1);
						}else {
							int nbchgtsClient = calculNbChgts(clientMdlOriginal, clientMdl);
							clientMdl.setNbchgts(nbchgtsClient);
							if(nbchgtsClient>0) {
								nbchgtsTournee ++;
							}
						}
					}
					int nbretraits = calculNbRetraits(tourneeInfoMdl.getClients(), mapClientMdl);
					tourneeInfoMdl.setNbchgts(nbchgtsTournee + nbretraits);
					if(tourneeInfoMdl.getNbchgts()>0) {
						log.debug("nbchgtsTournee {}",nbchgtsTournee);
						log.debug("nbretraits {}",nbretraits);
						log.debug("tourneeInfoMdl {}",tourneeInfoMdl);
					}
				}
			}
		}catch(Exception e) {
			log.error("detecteChangements {}",serviceInfoMdl,e);
		}
		return serviceInfoMdlCheck;
	}
	
	private int calculNbRetraits(final List<ClientMdl> clients, final Map<String,ClientMdl> mapClientMdlOriginal) {
		int nbretraits = 0;
		String tour = null;
		if(!clients.isEmpty()) {
			tour = clients.get(0).getTour();
		}
		int nbcletour = 0;
		for(String cle : mapClientMdlOriginal.keySet()) {
			if(cle.startsWith(tour+"-")) {
				nbcletour++;
			}
		}
		if(nbcletour>clients.size()) {
			nbretraits = nbcletour - clients.size();
		}
		return nbretraits;
	}
	
	private int calculNbChgts(final ClientMdl clientMdlOriginal, final ClientMdl clientMdl) {
		int nbchgts = 0;
		Map<String,OperationMdl> mapOperationMdl = new HashMap<>();
		for(OperationMdl operationMdl : clientMdlOriginal.getOperations()) {
			String cle = operationMdl.getId() + "-" + operationMdl.getDispoid();
			mapOperationMdl.put(cle,operationMdl);
		}
		for(OperationMdl operationMdl : clientMdl.getOperations()) {
			String cle = operationMdl.getId() + "-" + operationMdl.getDispoid();
			OperationMdl operationMdlOriginal = mapOperationMdl.get(cle);
			if(operationMdlOriginal==null) {
				nbchgts ++;
			}else {
				if(operationMdlOriginal.getQteprev()!=null && operationMdl.getQteprev()!=null 
						&& !operationMdlOriginal.getQteprev().equals(operationMdl.getQteprev())) {
					nbchgts ++;
				}
			}
		}
		
		return nbchgts;
	}
	
	private Map<String,ClientMdl> mapClientMdl(final ServiceInfoMdl serviceInfoMdlOriginal){
		Map<String,ClientMdl> mapClientMdl = new HashMap<>();
		for(TourneeInfoMdl tourneeInfoMdl : serviceInfoMdlOriginal.getTournees()) {
			for(ClientMdl clientMdl : tourneeInfoMdl.getClients()) {
				String cle = clientMdl.getTour() + "-" + clientMdl.getId();
				mapClientMdl.put(cle, clientMdl);
			}
		}
		return mapClientMdl;
	}
	
	private String stockeServiceInfo(final ServiceInfoMdl serviceInfoMdl, final String codeservice, final String sid) {
		String urljson = null;
		try {
			String result = objectMapper.writeValueAsString(serviceInfoMdl);
			byte[] data = result.getBytes();			
			InputStream inputStream = new ByteArrayInputStream(data, 0, data.length);
			
			String bucket = Constantes.S3_BUCKET;
			String filename = codeservice + "-" + sid + "-" + System.currentTimeMillis() + Constantes.FLUXSVCINFO_EXT;
	        String key = Constantes.S3_FLUXSVCINFO_FOLDER + filename;
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(Constantes.FLUXSVCINFO_CONTENT_TYPE);
	        metadata.setContentLength(data.length);
			
			PutObjectRequest pRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
			pRequest.setCannedAcl(CannedAccessControlList.PublicRead);
			
			amazonS3.putObject(pRequest);
			urljson = Constantes.S3_FLUXSVCINFO_URL + filename;
		}catch(Exception e) {
			log.error("stockeServiceInfo {}",serviceInfoMdl,e);
		}
		return urljson;
	}
}
