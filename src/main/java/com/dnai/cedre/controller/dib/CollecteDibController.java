package com.dnai.cedre.controller.dib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.controller.ParentController;
import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.model.dib.ClientdibMdl;
import com.dnai.cedre.service.CollecteDibService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CollecteDibController extends ParentController{

	@Autowired
	private CollecteDibService collecteDibService;
	
	@RequestMapping(value = "finalisecollectedib", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl finalisecollectedib(@RequestBody ClientdibMdl clientMdl, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		try {
			collecteDibService.finaliseClient(clientMdl);
		} catch (Exception e) {
			log.error("finalisecollectedib : {}",clientMdl,e);
		}
		ResultGenericMdl resultGenericMdl = new ResultGenericMdl();
		resultGenericMdl.setCode(HttpServletResponse.SC_OK);
		return resultGenericMdl;
	}
}
