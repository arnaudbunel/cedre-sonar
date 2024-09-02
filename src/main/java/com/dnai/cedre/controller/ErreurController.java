package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ErreurClientMdl;
import com.dnai.cedre.model.ResultGenericMdl;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ErreurController extends ParentController{
	
	@RequestMapping(value = "erreur", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl erreur(@RequestBody ErreurClientMdl erreurClientMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = getToken(httpServletRequest);
			log.warn("Erreur client : " + erreurClientMdl + ", avec token :" + token);
		} catch (Exception e) {
			log.error(e.toString() + " avec erreurClientMdl: " + erreurClientMdl);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	// TODO supprimer appel côté front
	@RequestMapping(value = "debug", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl debug(@RequestBody ErreurClientMdl erreurClientMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			String token = getToken(httpServletRequest);
			log.debug("Debug client : " + erreurClientMdl + ", avec token :" + token);
			if(token!=null) {
				//contexteService.majDebug(erreurClientMdl, token);
			}else {
				log.error("debug : Debug client : " + erreurClientMdl + ", avec token null");
			}
		} catch (Exception e) {
			log.error(e.toString() + " avec erreurClientMdl: " + erreurClientMdl);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
