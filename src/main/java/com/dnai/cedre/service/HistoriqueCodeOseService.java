package com.dnai.cedre.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.HistoriqueCodeOseRepository;
import com.dnai.cedre.domain.HistoriqueCodeOse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HistoriqueCodeOseService {

	@Autowired
	private HistoriqueCodeOseRepository historiqueCodeOseRepository;
	
	@Transactional
	public void addHistoriqueCodeOse(String codeservice, String tour, String idose) {
		try {
			if(codeservice!=null) {
				HistoriqueCodeOse historiqueCodeOse = new HistoriqueCodeOse();
				historiqueCodeOse.setCodeservice(codeservice.toUpperCase());
				historiqueCodeOse.setDatetournee(new Date());
				historiqueCodeOse.setIdose(idose);
				historiqueCodeOse.setTour(tour);
				
				historiqueCodeOseRepository.save(historiqueCodeOse);
			}
		}catch(Exception e) {
			log.error("addHistoriqueCodeOse : codeservice {}, idose {}",codeservice,idose,e);
		}
	}
}
