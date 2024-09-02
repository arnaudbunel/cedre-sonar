package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.cockpit.AgentoseMdl;
import com.dnai.cedre.model.cockpit.ClientoseCritereMdl;
import com.dnai.cedre.model.cockpit.ClientoseMdl;
import com.dnai.cedre.model.cockpit.MandantMdl;
import com.dnai.cedre.service.AgentoseService;
import com.dnai.cedre.service.ClientoseService;
import com.dnai.cedre.service.MandantService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitCommunController {
	
	@Autowired
	private ClientoseService clientoseService;
	
	@Autowired
	private AgentoseService agentoseService;
	
	@Autowired
	private MandantService mandantService;
	
	@PostMapping(value = "clients", consumes = "application/json; charset=UTF-8")
	public List<ClientoseMdl> clients(@RequestBody ClientoseCritereMdl clientoseCritereMdl,HttpServletResponse httpServletResponse){
		try {			
			return clientoseService.clientsose(clientoseCritereMdl);
		}catch (Exception e) {
			log.error("clients " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@GetMapping(value = "agents")
	public List<AgentoseMdl> agents(HttpServletResponse httpServletResponse){
		try {			
			return agentoseService.agentsose();
		}catch (Exception e) {
			log.error("agents " + e.toString());
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	
	@GetMapping(value = "mandants")
	public List<MandantMdl> mandants(HttpServletResponse httpServletResponse){
		try {			
			return mandantService.mandants();
		}catch (Exception e) {
			log.error("mandants",e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
}
