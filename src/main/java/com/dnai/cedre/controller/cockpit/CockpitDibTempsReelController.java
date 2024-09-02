package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.DibTempsReelMdl;
import com.dnai.cedre.model.cockpit.TempsreelCritereMdl;
import com.dnai.cedre.service.DibTempsReelService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitDibTempsReelController {

	@Autowired
	private DibTempsReelService dibTempsReelService;
	
	@PostMapping(value = "dibtempsreel", consumes = "application/json; charset=UTF-8")
	public List<DibTempsReelMdl> dibtempsreel(@RequestBody TempsreelCritereMdl tempsreelCritereMdl, HttpServletResponse httpServletResponse){
		try {			
			return dibTempsReelService.tempsreel(tempsreelCritereMdl);
		}catch (Exception e) {
			log.error("dibtempsreel {}",tempsreelCritereMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
