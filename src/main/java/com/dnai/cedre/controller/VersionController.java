package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ResultGenericMdl;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class VersionController {

	@Autowired
	private Environment env;
	
	@GetMapping(value = "chgtversion/{ver}")
	public ResultGenericMdl chgtversion(@PathVariable String ver,HttpServletResponse httpServletResponse){
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		try {
			log.debug("chgtversion : ver : " + ver);
			String versionclient = env.getProperty("versionclient");
			if(versionclient!=null && !versionclient.equals(ver)) {
				log.debug("changement version : ver : " + ver + " / versionclient : " + versionclient);
				resultGenericMdl.setCode(HttpServletResponse.SC_OK);
			}else {
				resultGenericMdl.setCode(HttpServletResponse.SC_NO_CONTENT);
			}
		}catch (Exception e) {
			log.error("clients " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return resultGenericMdl;
	}
	
	@GetMapping(value = "chgtversiondib/{ver}")
	public ResultGenericMdl chgtversiondib(@PathVariable String ver,HttpServletResponse httpServletResponse){
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		try {
			log.debug("chgtversion : ver : " + ver);
			String versionclient = env.getProperty("versionclientdib");
			if(versionclient!=null && !versionclient.equals(ver)) {
				log.debug("changement version dib : ver : " + ver + " / versionclientdib : " + versionclient);
				resultGenericMdl.setCode(HttpServletResponse.SC_OK);
			}else {
				resultGenericMdl.setCode(HttpServletResponse.SC_NO_CONTENT);
			}
		}catch (Exception e) {
			log.error("chgtversiondib " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return resultGenericMdl;
	}
	
}
