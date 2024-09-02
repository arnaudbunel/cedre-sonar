package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ParentController {
	
	protected String getToken(HttpServletRequest request) {
		String token = null;
		String headerauthorization = request.getHeader(Constantes.TOKEN_HTTP_HEADER_KEY);
		if(headerauthorization!=null) {
			token = headerauthorization.replace(Constantes.TOKEN_HTTP_HEADER_BEARER_KEY, "").trim();
		}else {
			log.warn("getToken : headerauthorization null dans : " + Constantes.TOKEN_HTTP_HEADER_KEY);
			token = request.getHeader(Constantes.HEADER_ADNAI_TOKEN);
			if(token==null) {
				log.warn("getToken : Adnai-Token null");
			}else {
				log.debug("getToken : Adnai-Token : " + token);
			}
		}
		return token;
	}
}
