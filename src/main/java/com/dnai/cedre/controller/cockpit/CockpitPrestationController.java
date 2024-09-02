package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.PrestationCritereMdl;
import com.dnai.cedre.model.cockpit.PrestationMdl;
import com.dnai.cedre.service.PrestationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitPrestationController {
	
	@Autowired
	private PrestationService prestationService;

	@PostMapping(value = "prestations", consumes = "application/json; charset=UTF-8")
	public List<PrestationMdl> prestations(@RequestBody PrestationCritereMdl prestationCritereMdl,HttpServletResponse httpServletResponse){
		try {			
			return prestationService.prestations(prestationCritereMdl);
		}catch (Exception e) {
			log.error("prestations " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
