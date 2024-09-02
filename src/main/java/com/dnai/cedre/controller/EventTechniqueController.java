package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.EventTechniqueMdl;
import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.service.EventTechniqueService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EventTechniqueController extends ParentController{
	
	@Autowired
	private EventTechniqueService eventTechniqueService;

	@PostMapping(value = "saveeventtechnique", consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl saveEventTechnique(@RequestBody EventTechniqueMdl eventTechniqueMdl, HttpServletRequest httpServletRequest){
		try {
			String token = getToken(httpServletRequest);
			eventTechniqueMdl.setToken(token);
			eventTechniqueService.save(eventTechniqueMdl);
		}catch (Exception e) {
			log.error("saveEventTechnique {}",eventTechniqueMdl,e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
