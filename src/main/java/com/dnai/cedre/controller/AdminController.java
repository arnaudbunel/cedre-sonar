package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.service.CleanDataService;
import com.dnai.cedre.service.TranscoSignalementService;

import lombok.extern.slf4j.Slf4j;

@RestController()
@Slf4j
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private TranscoSignalementService transcoSignalementService;
	
	@Autowired
	private CleanDataService cleanDataService;
	
	@GetMapping("/updatetranscosignalements")
	public ResultGenericMdl updateTranscoSignalements(){
		try {			
			transcoSignalementService.updateTranscoSignalements();
		}catch (Exception e) {
			log.error("updateTranscoSignalements",e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	@GetMapping("/cleandata")
	public ResultGenericMdl cleandata(){
		try {			
			cleanDataService.cleanData();
		}catch (Exception e) {
			log.error("cleandata",e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
