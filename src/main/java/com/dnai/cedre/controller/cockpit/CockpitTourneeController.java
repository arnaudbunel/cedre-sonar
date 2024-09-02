package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.CollecteMdl;
import com.dnai.cedre.model.cockpit.TourneeCritereMdl;
import com.dnai.cedre.model.cockpit.TourneeMdl;
import com.dnai.cedre.service.TourneeencoursService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitTourneeController {

	@Autowired
	private TourneeencoursService tourneeencoursService;
	
	@PostMapping(value = "tourneeencours", consumes = "application/json; charset=UTF-8")
	public List<TourneeMdl> tourneeencours(@RequestBody TourneeCritereMdl tourneeCritereMdl, HttpServletResponse httpServletResponse){
		try {			
			return tourneeencoursService.tourneeencours(tourneeCritereMdl);
		}catch (Exception e) {
			log.error("tourneeencours {}",tourneeCritereMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@PostMapping(value = "savecollecteencours", consumes = "application/json; charset=UTF-8")
	public CollecteMdl savecollecteencours(@RequestBody CollecteMdl collecteMdl, HttpServletResponse httpServletResponse){
		try {			
			return tourneeencoursService.savecollecteencours(collecteMdl);
		}catch (Exception e) {
			log.error("savecollecteencours {}",collecteMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@PostMapping(value = "savetourneeencours", consumes = "application/json; charset=UTF-8")
	public TourneeMdl savetourneeencours(@RequestBody TourneeMdl tourneeMdl, HttpServletResponse httpServletResponse){
		try {			
			return tourneeencoursService.savetourneeencours(tourneeMdl);
		}catch (Exception e) {
			log.error("savetourneeencours {}",tourneeMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
