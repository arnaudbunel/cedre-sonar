package com.dnai.cedre.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.EventTourneeRepository;
import com.dnai.cedre.dao.MessageAtlasRepository;
import com.dnai.cedre.domain.EventTournee;
import com.dnai.cedre.domain.MessageAtlas;
import com.dnai.cedre.model.EventTourneeMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EventTourneeService {
	
	@Autowired
	private EventTourneeRepository eventTourneeRepository;
	
	@Autowired
	private MessageAtlasRepository messageAtlasRepository;
	
	@Transactional
	public List<EventTourneeMdl> eventsnonrecu() {
		List<EventTourneeMdl> eventsnonrecu = new ArrayList<>();
		try {
			List<EventTournee> events = eventTourneeRepository.findByDhreceptionIsNull();
			for(EventTournee event : events) {
				if(event.getDhsaisie().isBefore(LocalDateTime.now().minusMinutes(1))) {
					EventTourneeMdl eventTourneeMdl = new EventTourneeMdl();
					eventTourneeMdl.setEventtype(event.getEvent());
					eventTourneeMdl.setIdevent(event.getId());
					eventTourneeMdl.setIdtournee(event.getIdtournee());
					eventsnonrecu.add(eventTourneeMdl);
				}
			}
		}catch(Exception e) {
			log.error("eventsnonrecu",e);
		}
		return eventsnonrecu;
	}
	

	@Transactional
	public void marquerCommeRecu(final EventTourneeMdl eventTourneeMdl) {
		try {
			if(Constantes.EVENT_TOURNEE_MESSAGE.equals(eventTourneeMdl.getEventtype())) {
				MessageAtlas messageAtlas = messageAtlasRepository.findById(eventTourneeMdl.getIdmessage()).get();
				messageAtlas.setDhreception(LocalDateTime.now());
				messageAtlasRepository.save(messageAtlas);
			}else if(Constantes.EVENT_TOURNEE_FUSION.equals(eventTourneeMdl.getEventtype())) {
				EventTournee event = eventTourneeRepository.findById(eventTourneeMdl.getIdevent()).get();
				event.setDhreception(LocalDateTime.now());
				eventTourneeRepository.save(event);
			}
		}catch(Exception e) {
			log.error("marquerCommeRecu {}",eventTourneeMdl,e);
		}
	}
	
	@Transactional
	public void marquerCommeAction(final long idtournee) {
		try {
			EventTournee event = eventTourneeRepository.findFirstByIdtourneeAndDhreceptionIsNotNullAndDhactionIsNull(idtournee);
			if(event!=null) {
				event.setDhaction(LocalDateTime.now());
				eventTourneeRepository.save(event);
			}
		}catch(Exception e) {
			log.error("marquerCommeAction {}",idtournee,e);
		}
	}
}
