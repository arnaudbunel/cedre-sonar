package com.dnai.cedre.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.EventTourneeMdl;
import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.service.EventTourneeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EventCollecteLogController {
	@Autowired
	private EventTourneeService eventTourneeService;
		
	@GetMapping(value = "eventsnonrecu")
	public List<EventTourneeMdl> eventsnonrecu(){
		try {
			return eventTourneeService.eventsnonrecu();
		}catch (Exception e) {
			log.error("eventsnonrecu",e);
		}
		return null;
	}
	
	@PostMapping(value = "eventrecu", consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl marquerCommeRecu(@RequestBody EventTourneeMdl eventTourneeMdl){
		try {
			eventTourneeService.marquerCommeRecu(eventTourneeMdl);
		}catch (Exception e) {
			log.error("marquerCommeRecu {}",eventTourneeMdl,e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
