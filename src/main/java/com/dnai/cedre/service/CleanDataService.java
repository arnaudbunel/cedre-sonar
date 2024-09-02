package com.dnai.cedre.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.EventTechniqueRepository;
import com.dnai.cedre.dao.EventTourneeRepository;
import com.dnai.cedre.dao.HistoriqueCodeOseRepository;
import com.dnai.cedre.dao.HistoriqueTourneeRepository;
import com.dnai.cedre.dao.MessageAtlasRepository;
import com.dnai.cedre.dao.PositionevtRepository;
import com.dnai.cedre.domain.EventTechnique;
import com.dnai.cedre.domain.EventTournee;
import com.dnai.cedre.domain.HistoriqueCodeOse;
import com.dnai.cedre.domain.HistoriqueTournee;
import com.dnai.cedre.domain.MessageAtlas;
import com.dnai.cedre.domain.Positionevt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CleanDataService {
	
	@Autowired
	private HistoriqueTourneeRepository historiqueTourneeRepository;
	
	@Autowired
	private HistoriqueCodeOseRepository historiqueCodeOseRepository;
	
	@Autowired
	private EventTechniqueRepository eventTechniqueRepository;
	
	@Autowired
	private EventTourneeRepository eventTourneeRepository;
	
	@Autowired
	private MessageAtlasRepository messageAtlasRepository;

	@Autowired
	private PositionevtRepository positionevtRepository;
	
	@Transactional
	public void cleanData() {
		try {		
			LocalDateTime ldtSeuil = LocalDateTime.now().minusDays(60);
			Date dSeuil = Date.from(ldtSeuil.atZone(ZoneId.systemDefault()).toInstant());

			// HistoriqueTournee
			List<HistoriqueTournee> historiqueTourneeToDelete = historiqueTourneeRepository.findByDhcreationBefore(ldtSeuil);
			historiqueTourneeRepository.deleteAll(historiqueTourneeToDelete);
			log.info("cleanData, HistoriqueTournee : {} éléments supprimés",historiqueTourneeToDelete.size());
			
			// HistoriqueCodeOse
			List<HistoriqueCodeOse> historiqueCodeOseToDelete = historiqueCodeOseRepository.findByDatetourneeBefore(dSeuil);
			historiqueCodeOseRepository.deleteAll(historiqueCodeOseToDelete);
			log.info("cleanData, HistoriqueCodeOse : {} éléments supprimés",historiqueCodeOseToDelete.size());

			// EventTechnique
			List<EventTechnique> eventTechniqueToDelete = eventTechniqueRepository.findByDheventBefore(ldtSeuil);
			eventTechniqueRepository.deleteAll(eventTechniqueToDelete);
			log.info("cleanData, EventTechnique : {} éléments supprimés",eventTechniqueToDelete.size());
						
			// EventTournee
			List<EventTournee> eventTourneeToDelete = eventTourneeRepository.findByDhsaisieBefore(ldtSeuil);
			eventTourneeRepository.deleteAll(eventTourneeToDelete);
			log.info("cleanData, EventTournee : {} éléments supprimés",eventTourneeToDelete.size());

			// MessageAtlas
			List<MessageAtlas> messageAtlasToDelete = messageAtlasRepository.findByDhsaisieBefore(ldtSeuil);
			messageAtlasRepository.deleteAll(messageAtlasToDelete);
			log.info("cleanData, MessageAtlas : {} éléments supprimés",messageAtlasToDelete.size());
			
			// Positionevt
			List<Positionevt> positionevtToDelete = positionevtRepository.findByDhevtBefore(dSeuil);
			positionevtRepository.deleteAll(positionevtToDelete);
			log.info("cleanData, Positionevt : {} éléments supprimés",positionevtToDelete.size());			
		}catch(Exception e) {
			log.error("cleanData",e);
		}
	}
}
