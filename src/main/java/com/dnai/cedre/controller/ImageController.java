package com.dnai.cedre.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ImageController {
	
	@Autowired
	private MediaService mediaService;
	
	@RequestMapping(value = "imageupload", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public MediaMdl imageupload(@RequestBody MediaMdl mediaMdl, HttpServletResponse httpServletResponse){
		try {	        
	        return mediaService.enregistreMedia(mediaMdl);
		} catch (Exception e) {
			log.error("imageupload : mediaMdl : {}",mediaMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "imageuploaddib", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public MediaMdl imageuploaddib(@RequestBody MediaMdl mediaMdl, HttpServletResponse httpServletResponse){
		try {	        
	        return mediaService.enregistreMediaDib(mediaMdl);
		} catch (Exception e) {
			log.error("imageuploaddib : mediaMdl {}",mediaMdl,e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "imagedelete", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl imagedelete(@RequestBody MediaMdl mediaMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			mediaService.supprimeMedia(mediaMdl);
		} catch (Exception e) {
			log.error("imagedelete : mediaMdl {}",mediaMdl,e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	@RequestMapping(value = "imagedeletedib", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl imagedeletedib(@RequestBody MediaMdl mediaMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			mediaService.supprimeMediaDib(mediaMdl);
		} catch (Exception e) {
			log.error("imagedeletedib : mediaMdl {}",mediaMdl,e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
