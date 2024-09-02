package com.dnai.cedre.controller.cockpit;

import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.model.cockpit.AnomalieCritereMdl;
import com.dnai.cedre.model.cockpit.MailanoMdl;
import com.dnai.cedre.model.suivi.AnoclientMdl;
import com.dnai.cedre.model.suivi.TourneeSuiviMdl;
import com.dnai.cedre.service.AnomalieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Slf4j
public class CockpitAnomalieController {
	
	@Autowired
	private AnomalieService anomalieService;
	
	//@Autowired
	//private AdnAiUtilsService adnAiUtilsService;
	
	@RequestMapping(value = "tourneesuivi", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public List<TourneeSuiviMdl> tourneesuivi(@RequestBody AnomalieCritereMdl anomalieCritereMdl, HttpServletResponse httpServletResponse){
		try {			
			return anomalieService.tourneesuivi(anomalieCritereMdl);
		}catch (Exception e) {
			log.error(e.toString() + " avec anomalieCritereMdl: " + anomalieCritereMdl);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "anomalie", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public List<AnoclientMdl> anomalies(@RequestBody AnomalieCritereMdl anomalieCritereMdl, HttpServletResponse httpServletResponse){
		try {			
			return anomalieService.anomalies(anomalieCritereMdl);
		}catch (Exception e) {
			log.error(e.toString() + " avec anomalieCritereMdl: " + anomalieCritereMdl);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "mailanomalie", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl mailanomalie(@RequestBody MailanoMdl mailanoMdl, HttpServletResponse httpServletResponse){
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		try {			

            /*EmailRequest emailRequest = new EmailRequest();
            emailRequest.setCodeconfiguration("cedre1");
            emailRequest.setSubject("Cedre Atlas : Photos anomalies");

            emailRequest.getContenus().put("datefmt", mailanoMdl.getDatefmt());
            emailRequest.getContenus().put("libequipe", mailanoMdl.getLibequipe());
            emailRequest.getContenus().put("nomclient", mailanoMdl.getNomclient());
            
            // on gère jusqu'à 10 photos
            int nbphotos = mailanoMdl.getUrlsphoto().size();
            for(int i=1;i<=10 && i<=nbphotos;i++) {
            	String clephoto = "photo" + i;
            	String urlphoto = mailanoMdl.getUrlsphoto().get(i-1);
            	emailRequest.getContenus().put(clephoto, urlphoto);
            }
          
            String[] tabdestto = mailanoMdl.getDestinataires().split(",");
            
        	for(String destto : tabdestto) {
	            EmailDestinataire emailDestinataire = new EmailDestinataire(destto.trim(),"TO");
	            emailRequest.getDestinataires().add(emailDestinataire);
        	}
            
            adnAiUtilsService.sendEmail(emailRequest);*/
			log.info("mailanomalie : " + mailanoMdl);
		}catch (Exception e) {
			log.error("mailanomalie : " + e.toString() + " avec mailanoMdl : " + mailanoMdl);
			resultGenericMdl.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return resultGenericMdl;
	}
}
