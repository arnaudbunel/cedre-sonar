package com.dnai.cedre.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.EventTechniqueRepository;
import com.dnai.cedre.domain.EventTechnique;
import com.dnai.cedre.model.EventTechniqueMdl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventTechniqueService extends ParentService{
	
	@Autowired
	private EventTechniqueRepository eventTechniqueRepository;

	@Transactional
	public void save(final EventTechniqueMdl eventTechniqueMdl) {
		try {
			EventTechnique eventTechnique = new EventTechnique();
			eventTechnique.setCodeservice(eventTechniqueMdl.getCodeservice());
			eventTechnique.setDataevt(eventTechniqueMdl.getDataevt());
			eventTechnique.setDhevent(longToLocalDateTime(eventTechniqueMdl.getDhevent()));
			eventTechnique.setEvt(eventTechniqueMdl.getEvt());
			eventTechnique.setIdadnai(eventTechniqueMdl.getIdadnai());
			eventTechnique.setToken(eventTechniqueMdl.getToken());
			eventTechniqueRepository.save(eventTechnique);
		}catch(Exception e) {
			log.error("save, eventTechniqueMdl {}",eventTechniqueMdl,e);
		}
	}
}
