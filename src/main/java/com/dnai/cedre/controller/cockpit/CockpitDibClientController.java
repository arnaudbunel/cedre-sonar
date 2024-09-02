package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.DibCockpitClientMdl;
import com.dnai.cedre.model.cockpit.PrestationCritereMdl;
import com.dnai.cedre.service.DibCockpitClientService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitDibClientController {

	@Autowired
	private DibCockpitClientService dibCockpitClientService;
	
	@PostMapping(value = "dibclients", consumes = "application/json; charset=UTF-8")
	public List<DibCockpitClientMdl> dibclients(@RequestBody PrestationCritereMdl prestationCritereMdl,HttpServletResponse httpServletResponse){
		try {			
			return dibCockpitClientService.prestations(prestationCritereMdl);
		}catch (Exception e) {
			log.error("dibclients ",e);
			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
