package com.dnai.cedre.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.MessageAtlasCreationMdl;
import com.dnai.cedre.model.MessageAtlasLectureMdl;
import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.service.MessageAtlasService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MessageAtlasController {
	
	@Autowired
	private MessageAtlasService messageAtlasService;
		
	@GetMapping(value = "messagesnonrecu")
	public List<MessageAtlasLectureMdl> messagesnonrecu(){
		try {
			return messageAtlasService.messagesnonrecu();
		}catch (Exception e) {
			log.error("messagesnonrecu",e);
		}
		return null;
	}
	
	@PostMapping(value = "creationmessage", consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl creationMessage(@RequestBody MessageAtlasCreationMdl messageAtlasCreationMdl){
		try {
			messageAtlasService.creationMessage(messageAtlasCreationMdl);
		}catch (Exception e) {
			log.error("creationMessage " + e.toString());
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
	
	@PostMapping(value = "marquercommelu", consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl marquerCommeLu(@RequestBody MessageAtlasLectureMdl messageAtlasLectureMdl){
		try {			
			messageAtlasService.marquerCommeLu(messageAtlasLectureMdl);
		}catch (Exception e) {
			log.error("marquerCommeLu " + e.toString());
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
