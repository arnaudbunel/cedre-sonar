package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ClientMdl;
import com.dnai.cedre.model.MajTourneeOseMdl;
import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.model.ServiceInfoMdl;
import com.dnai.cedre.model.TourneeInfoCritereMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.model.ose.GetSceResponseMdl;
import com.dnai.cedre.service.DeroulementService;
import com.dnai.cedre.service.EventTourneeService;
import com.dnai.cedre.service.OseService;
import com.dnai.cedre.service.ParentService;
import com.dnai.cedre.service.TokenService;
import com.dnai.cedre.service.TourneeService;
import com.dnai.cedre.service.TourneeSuiviService;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TourneeController extends ParentController {

	@Autowired
	private TourneeService tourneeService;
	
	@Autowired
	private TokenService tokenService;
		
	@Autowired
	private OseService majOseService;
	
	@Autowired
	private ParentService parentService;
	
	@Autowired
	private TourneeSuiviService tourneeSuiviService;
	
	@Autowired
	private EventTourneeService eventTourneeService;
	
	@Autowired
	private DeroulementService deroulementService;

	@Autowired
	private OseService oseService;
	
	@RequestMapping(value = "serviceinfo", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ServiceInfoMdl serviceInfo(@RequestBody TourneeInfoCritereMdl tourneeInfoCritereMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = tokenService.calculToken(httpServletRequest);
			ServiceInfoMdl serviceInfoMdl = tourneeService.serviceInfo(tourneeInfoCritereMdl, token, httpServletRequest.getHeader(Constantes.HEADER_ADNAI_VERSION));
			if(serviceInfoMdl!=null){
				return serviceInfoMdl;
			}else{
				log.warn("serviceInfo : pas de resultat, {}",tourneeInfoCritereMdl);
				httpServletResponse.setStatus( HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
		} catch (Exception e) {
			log.error("serviceInfo {}",tourneeInfoCritereMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	
	@RequestMapping(value = "trnnext", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public TourneeInfoMdl trnnext(@RequestBody TourneeInfoCritereMdl tourneeInfoCritereMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = getToken(httpServletRequest);
			TourneeInfoMdl tourneeInfo = null;
			if(token!=null) {
				tourneeInfo = tourneeService.preparationProchaineTournee(tourneeInfoCritereMdl, token);
			}

			if(tourneeInfo!=null){
				return tourneeInfo;
			}else{
				log.error("trnnext : pas de resultat pour tourneeInfoCritereMdl: " + tourneeInfoCritereMdl);
				httpServletResponse.setStatus( HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
		} catch (Exception e) {
			log.error(e.toString() + " avec tourneeInfoCritereMdl: " + tourneeInfoCritereMdl);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

	@RequestMapping(value = "trnfusion", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public TourneeInfoMdl trnfusion(@RequestBody TourneeInfoMdl tourneeMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		try {
			String token = getToken(httpServletRequest);
			TourneeInfoMdl tourneeInfo = tourneeMdl;
			if(token!=null) {
				GetSceResponseMdl getSceResponseMdl = oseService.getServiceOse(token);
				if(getSceResponseMdl!=null) {
					tourneeInfo = tourneeService.tourneeFusion(tourneeMdl, getSceResponseMdl);					
					// contexteService.majTournee(tourneeInfo, token);
					oseService.majDeroulementTourneeMdl(token, tourneeInfo, null,null);
					eventTourneeService.marquerCommeAction(tourneeInfo.getIdadnai());
				}else {
					log.error("trnfusion : getSceResponseMdl null pour token {}",token);
				}
			}
			return tourneeInfo;
		} catch (Exception e) {
			log.error("trnfusion {}",tourneeMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "trninfo", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public TourneeInfoMdl trninfo(@RequestBody TourneeInfoCritereMdl tourneeInfoCritereMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			log.debug("trninfo : User-Agent : {}",httpServletRequest.getHeader("User-Agent"));
			String token = getToken(httpServletRequest);
			TourneeInfoMdl tourneeInfo = null;
			if(token!=null) {
				GetSceResponseMdl getSceResponseMdl = oseService.getServiceOse(token);
				if(getSceResponseMdl!=null) {
					tourneeInfo = tourneeService.tourneeInfo(tourneeInfoCritereMdl, getSceResponseMdl , token);
					tourneeInfo.setToken(token);
					tourneeInfo = tourneeService.creerTournee(tourneeInfo, getSceResponseMdl);

					oseService.majDeroulementTourneeMdl(token, tourneeInfo, null,null);
					majOseService.majDebutTournee(tourneeInfo, token);
				}else {
					log.error("trninfo : getSceResponseMdl null pour token : " + token);
				}
			}
			
			if(tourneeInfo!=null){
				return tourneeInfo;
			}else{
				log.error("trninfo : pas de resultat pour tourneeInfoCritereMdl: " + tourneeInfoCritereMdl);
				httpServletResponse.setStatus( HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
		} catch (Exception e) {
			log.error(e.toString() + " avec tourneeInfoCritereMdl: " + tourneeInfoCritereMdl);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "clientfin", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl clientfin(@RequestBody ClientMdl clientMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = getToken(httpServletRequest);
			if(token!=null) {
				majOseService.majFinClient(clientMdl, token);
			}else {
				log.error("clientfin : token null");
			}
			tourneeSuiviService.finaliseClient(clientMdl);
		} catch (Exception e) {
			log.error("clientfin : {}",clientMdl,e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	@RequestMapping(value = "tourneefin", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl tourneefin(@RequestBody TourneeInfoMdl tourneeMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = getToken(httpServletRequest);
			
			if(token==null) {
				log.warn("tourneefin : token dans headers null, récupération dans tourneeMdl");
				token = tourneeMdl.getToken();
				log.debug("tourneefin : token dans tourneeMdl : " + token);
			}
			if(token!=null) {
				for(ClientMdl clientMdl : tourneeMdl.getClients()) {
					if(clientMdl.getTmsmaj()==0) {
						majOseService.majDebutClient(clientMdl, token);
						majOseService.majFinClient(clientMdl, token);
					}
				}
				
				String cleServiceOse = deroulementService.getCleServiceOse(token);
				tourneeMdl = tourneeSuiviService.finaliseTournee(tourneeMdl, cleServiceOse);
			}else {
				log.error("tourneefin : token null pour tourneeMdl.getIdadnai : {}",tourneeMdl.getIdadnai());
				parentService.logJsonTournee(tourneeMdl);
			}
		} catch (Exception e) {
			log.error("tourneefin avec tourneeMdl : {}",tourneeMdl,e);
			parentService.logJsonTournee(tourneeMdl);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
		
	@RequestMapping(value = "clientdebut", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl clientdebut(@RequestBody ClientMdl clientMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = getToken(httpServletRequest);
			if(token!=null) {
				majOseService.majDebutClient(clientMdl, token);
			}else {
				log.error("clientdebut : token null");
			}
			tourneeSuiviService.debutClient(clientMdl);
		} catch (Exception e) {
			log.error("clientdebut : " + e.toString() + " avec clientMdl : " + clientMdl);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	@RequestMapping(value = "clientproximite", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl clientproximite(@RequestBody ClientMdl clientMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			tourneeSuiviService.proximiteClient(clientMdl);
		} catch (Exception e) {
			log.error("clientdebut : " + e.toString() + " avec clientMdl : " + clientMdl);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	@RequestMapping(value = "majtourneeoseasync", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl majTourneeOseAsync(@RequestBody MajTourneeOseMdl majTourneeOseMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		boolean retour = false;
		try {
			retour = majOseService.majTourneeOse(majTourneeOseMdl);
		} catch (Exception e) {
			log.error("majTourneeOseAsync : majTourneeOseMdl {}",majTourneeOseMdl,e);		
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(retour?HttpServletResponse.SC_OK:HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return resultGenericMdl;
	}
}
