package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.MediaMdl;
import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.service.MediaService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AudioController {

	@Autowired
	private MediaService mediaService;
	
	@CrossOrigin(origins = "http://localhost:4200")
	@RequestMapping(value = "audioupload", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public MediaMdl audioupload(@RequestBody MediaMdl mediaMdl, HttpServletResponse httpServletResponse){
	try {	        
	        return mediaService.enregistreMedia(mediaMdl);
		} catch (Exception e) {
			log.error("audioupload : " + e.toString() + " avec mediaMdl : " + mediaMdl);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "audiodelete", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl audiodelete(@RequestBody MediaMdl mediaMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			mediaService.supprimeMedia(mediaMdl);
		} catch (Exception e) {
			log.error(e.toString() + " avec mediaMdl: " + mediaMdl);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
