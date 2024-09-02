package com.dnai.cedre.service;

import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.dnai.cedre.model.DeroulementTourneeMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.model.ose.GetSceResponseMdl;
import com.dnai.cedre.util.Constantes;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeroulementService {

	@Autowired
	private OseService oseService;

	@Autowired
	private CommonUtilService commonUtilService;

    @Autowired
    @Setter(value = AccessLevel.PROTECTED)
    private DynamoDBMapper mapper;
	
    /*
	public void majDeroulementTourneeMdl(final String token, final TourneeInfoMdl tourneeInfo, final GetSceResponseMdl getSceResponseMdl, final String contexte) {
    	try {
    		DeroulementTourneeMdl deroulementTourneeMdl = getDeroulementTourneeMdl(token);
    		if(deroulementTourneeMdl!=null) {
    			if(tourneeInfo!=null) {
    				deroulementTourneeMdl.setTourneeMdl(tourneeInfo);
    			}
    			if(getSceResponseMdl!=null) {
    				StringWriter outputWriter = new StringWriter();
    				Serializer serializer = new Persister();
    				serializer.write(getSceResponseMdl, outputWriter);
    				String xml = outputWriter.toString();
    				
    				String clefluxose = oseService.stockeFluxOse(xml, deroulementTourneeMdl.getCodeservice());
     				deroulementTourneeMdl.setCleServiceOse(clefluxose);
    			}
    			if(contexte!=null) {
    				deroulementTourneeMdl.setContexte(contexte);
    			}
    			deroulementTourneeMdl.setLastmaj(commonUtilService.calculLastmaj());
    			mapper.save(deroulementTourneeMdl);
    		}
    	}catch(Exception e) {
			log.error("majTournee : {},{}",token,tourneeInfo,e);
    	}
    }*/
    
    public void majTourneeId(final String token, final String tourneeId) {
    	try {
    		DeroulementTourneeMdl deroulementTourneeMdl = getDeroulementTourneeMdl(token);
    		if(deroulementTourneeMdl!=null) {
    			deroulementTourneeMdl.setTourneeId(tourneeId);
    			deroulementTourneeMdl.setLastmaj(commonUtilService.calculLastmaj());
    			mapper.save(deroulementTourneeMdl);
    		}
    	}catch(Exception e) {
			log.error("majTourneeId : {},{}",token,tourneeId,e);
    	}
    }
    
    public String getCleServiceOse(final String token) {
    	String cleServiceOse = null;
		try {
    		DeroulementTourneeMdl deroulementTourneeMdl = getDeroulementTourneeMdl(token);
    		if(deroulementTourneeMdl!=null) {
    			cleServiceOse = deroulementTourneeMdl.getCleServiceOse();
    		}
		} catch (Exception e) {
			log.error("getServiceOse : {}",token,e);
		}
		return cleServiceOse;
    }
    
	/*public GetSceResponseMdl getServiceOse(final String token) {
		GetSceResponseMdl getSceResponseMdl = null;
		try {
    		DeroulementTourneeMdl deroulementTourneeMdl = getDeroulementTourneeMdl(token);
    		if(deroulementTourneeMdl!=null) {
    			getSceResponseMdl = oseService.getSceResponseFromS3(deroulementTourneeMdl.getCleServiceOse());
    		}
		} catch (Exception e) {
			log.error("getServiceOse : {}",token,e);
		}
		return getSceResponseMdl;
	}*/
    
    public DeroulementTourneeMdl getDeroulementTourneeMdl(final String token) {
    	DeroulementTourneeMdl deroulementTourneeMdl = null;
    	try {
    		deroulementTourneeMdl = this.mapper.load(DeroulementTourneeMdl.class, token);
    	}catch(Exception e) {
			log.error("getDeroulementTourneeMdl : {}",token,e);
    	}
    	return deroulementTourneeMdl;
    }
    
    public void initDeroulement(final String token, final String version, final String codeservice, final String cleServiceOse) {
    	try {
    		//long expirationStockage = Instant.now().plus(48,ChronoUnit.HOURS).getEpochSecond();
    		long expirationStockage = Instant.now().plus(15,ChronoUnit.DAYS).getEpochSecond();
    		DeroulementTourneeMdl deroulementTourneeMdl = new DeroulementTourneeMdl();
    		deroulementTourneeMdl.setToken(token);
    		deroulementTourneeMdl.setExpirationStockage(expirationStockage);
    		deroulementTourneeMdl.setVersion(version);
    		
            deroulementTourneeMdl.setLastmaj(commonUtilService.calculLastmaj());
            
            deroulementTourneeMdl.setContexte(Constantes.DRL_CTX_CHOIX_TOURNEE);
            deroulementTourneeMdl.setCodeservice(codeservice);
            // deroulementTourneeMdl.setServiceOse(getSceResponseMdl);
            deroulementTourneeMdl.setCleServiceOse(cleServiceOse);
            mapper.save(deroulementTourneeMdl);
    	}catch(Exception e) {
			log.error("initDeroulement : {},{}",token,version,e);
    	}
    }
}
