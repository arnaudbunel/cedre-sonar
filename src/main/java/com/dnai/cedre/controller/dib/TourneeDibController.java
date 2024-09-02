package com.dnai.cedre.controller.dib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.controller.ParentController;
import com.dnai.cedre.model.TourneeInfoCritereMdl;
import com.dnai.cedre.model.dib.TourneedibMdl;
import com.dnai.cedre.model.ose.GetSceResponseMdl;
import com.dnai.cedre.model.ose.SceTourneeMdl;
import com.dnai.cedre.service.OseService;
import com.dnai.cedre.service.TourneedibService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TourneeDibController extends ParentController{

	//@Autowired
	//private TokenService tokenService;
	
	@Autowired
	private OseService oseService;
	
	//@Autowired
	//private DeroulementService deroulementService;
	
	@Autowired
	private TourneedibService tourneedibService;
	
	@RequestMapping(value = "tourneedibinfo", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public TourneedibMdl tourneedibinfo(@RequestBody TourneeInfoCritereMdl tourneeInfoCritereMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			// verif si la tournée est déjà créée (attente pesée)
			TourneedibMdl tourneedibMdl = tourneedibService.rechercheTourneeDib(tourneeInfoCritereMdl.getSid());
			if(tourneedibMdl==null) {
			
				//String token = tokenService.calculToken(httpServletRequest);
				
				String result = oseService.getFluxOse(tourneeInfoCritereMdl.getSid());
				
				GetSceResponseMdl getSceResponseMdl = oseService.getSceResponse(result);
				
				if(getSceResponseMdl.getSce()!=null && !getSceResponseMdl.getSce().isEmpty()) {
					String cleflux = oseService.stockeFluxOse(result, tourneeInfoCritereMdl.getSid());
					SceTourneeMdl sceTournee = getSceResponseMdl.getSce().get(0);
					tourneedibMdl = tourneedibService.construitTourneeDib(sceTournee, tourneeInfoCritereMdl.getSid()/*, token*/, cleflux);
					
					// deroulementService.initDeroulement(token, httpServletRequest.getHeader(Constantes.HEADER_ADNAI_VERSION), tourneeInfoCritereMdl.getSid(), getSceResponseMdl);
				}
			}
			
			if(tourneedibMdl!=null){
				return tourneedibMdl;
			}else{
				log.error("tourneedibinfo : pas de resultat pour tourneeInfoCritereMdl: {}",tourneeInfoCritereMdl);
				httpServletResponse.setStatus( HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
			
		} catch (Exception e) {
			log.error("tourneedibinfo : tourneeInfoCritereMdl {}",tourneeInfoCritereMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "tourneedibmaj", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public TourneedibMdl tourneedibmaj(@RequestBody TourneedibMdl tourneedibMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			return tourneedibService.majTourneeDib(tourneedibMdl);
		} catch (Exception e) {
			log.error("tourneedibmaj : tourneedibMdl {}",tourneedibMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
