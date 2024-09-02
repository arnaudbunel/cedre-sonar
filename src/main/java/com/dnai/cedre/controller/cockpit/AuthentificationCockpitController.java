package com.dnai.cedre.controller.cockpit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.AuthentificationRetourMdl;
import com.dnai.cedre.model.cockpit.CredentialMdl;
import com.dnai.cedre.service.AuthentificationCockpitService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthentificationCockpitController {
	
	@Autowired
	private AuthentificationCockpitService authentificationCockpitService;
	
	@RequestMapping(value = "login", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public AuthentificationRetourMdl login(@RequestBody CredentialMdl credentialMdl, HttpServletResponse httpServletResponse){
		try {
			AuthentificationRetourMdl authentificationRetourMdl = authentificationCockpitService.authentificationCockpit(credentialMdl);

			if(authentificationRetourMdl!=null && authentificationRetourMdl.isAuthentifie()){
				return authentificationRetourMdl;
			}else{
				log.warn("pas de resultat pour credentialMdl: " + credentialMdl);
				httpServletResponse.setStatus( HttpServletResponse.SC_FORBIDDEN);
				return null;
			}
		} catch (Exception e) {
			log.error(e.toString() + " avec credentialMdl: " + credentialMdl);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
