package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.model.cockpit.TempsReelMdl;
import com.dnai.cedre.model.cockpit.TempsreelCritereMdl;
import com.dnai.cedre.model.cockpit.TourneeFusionCritereMdl;
import com.dnai.cedre.service.TempsReelService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitTempsReelController {
	
	@Autowired
	private TempsReelService tempsReelService;
	
	@PostMapping(value = "tempsreel", consumes = "application/json; charset=UTF-8")
	public List<TempsReelMdl> tempsreel(@RequestBody TempsreelCritereMdl tempsreelCritereMdl, HttpServletResponse httpServletResponse){
		try {			
			return tempsReelService.tempsreel(tempsreelCritereMdl);
		}catch (Exception e) {
			log.error("tempsreel " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@PostMapping(value = "demandetourneefusion", consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl demandetourneefusion(@RequestBody TourneeFusionCritereMdl tourneeFusionCritereMdl, HttpServletResponse httpServletResponse){
		try {			
			tempsReelService.demandeTourneeFusion(tourneeFusionCritereMdl);
		}catch (Exception e) {
			log.error("demandetourneefusion {}",tourneeFusionCritereMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
