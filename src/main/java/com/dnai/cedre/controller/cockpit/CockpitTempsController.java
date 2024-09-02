package com.dnai.cedre.controller.cockpit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.TempsCritereMdl;
import com.dnai.cedre.model.cockpit.TempsInfoMdl;
import com.dnai.cedre.service.TempsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitTempsController {

	@Autowired
	private TempsService tempsService;
	
	@PostMapping(value = "temps", consumes = "application/json; charset=UTF-8")
	public TempsInfoMdl temps(@RequestBody TempsCritereMdl tempsCritereMdl,HttpServletResponse httpServletResponse){
		try {			
			return tempsService.temps(tempsCritereMdl);
		}catch (Exception e) {
			log.error("temps " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
}
