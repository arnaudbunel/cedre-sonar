package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.EquipageMdl;
import com.dnai.cedre.model.cockpit.TrajetCritereMdl;
import com.dnai.cedre.model.cockpit.TrajetMdl;
import com.dnai.cedre.service.EquipageService;
import com.dnai.cedre.service.TrajetService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitTrajetController {

	@Autowired
	private EquipageService equipageService;
	
	@Autowired
	private TrajetService trajetService;
	
	@GetMapping(value = "equipages")
	public List<EquipageMdl> equipages(HttpServletResponse httpServletResponse){
		try {			
			return equipageService.equipages();
		}catch (Exception e) {
			log.error("equipages : " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@PostMapping(value = "trajets", consumes = "application/json; charset=UTF-8")
	public List<TrajetMdl> trajets(@RequestBody TrajetCritereMdl trajetCritereMdl,HttpServletResponse httpServletResponse){
		try {			
			return trajetService.trajets(trajetCritereMdl);
		}catch (Exception e) {
			log.error("trajets " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
