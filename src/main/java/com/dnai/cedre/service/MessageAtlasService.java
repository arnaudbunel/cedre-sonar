package com.dnai.cedre.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dnai.cedre.dao.MessageAtlasRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.MessageAtlas;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.EventTourneeMdl;
import com.dnai.cedre.model.MessageAtlasCreationMdl;
import com.dnai.cedre.model.MessageAtlasLectureMdl;
import com.dnai.cedre.util.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageAtlasService extends ParentService {
	
	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private MessageAtlasRepository messageAtlasRepository;
	
	@Autowired
	private AmazonSQS amazonSQS;
	
    @Autowired
    private ObjectMapper objectMapper;
	
	@Autowired
	private Environment env;
    
	@Transactional
	public void creationMessage(MessageAtlasCreationMdl messageAtlasCreationMdl) {
		try {
			Tournee tournee = tourneeRepository.findById(messageAtlasCreationMdl.getIdtournee()).get();
			
			MessageAtlas messageAtlas = new MessageAtlas();
			messageAtlas.setDhsaisie(LocalDateTime.now());
			messageAtlas.setLecture(false);
			messageAtlas.setMessage(messageAtlasCreationMdl.getMessage());
			messageAtlas.setTournee(tournee);
			MessageAtlas messageAtlasNew = messageAtlasRepository.save(messageAtlas);
						
			EventTourneeMdl eventTourneeMdl = new EventTourneeMdl();
			eventTourneeMdl.setDhfmtcreation(toLocalDateTimeFmt(messageAtlasNew.getDhsaisie()));
			eventTourneeMdl.setEventtype(Constantes.EVENT_TOURNEE_MESSAGE);
			eventTourneeMdl.setIdmessage(messageAtlasNew.getId());
			eventTourneeMdl.setMessage(messageAtlasNew.getMessage());
			eventTourneeMdl.setIdtournee(tournee.getId());
						
			SendMessageRequest sendMessageRequest = new SendMessageRequest(env.getProperty(Constantes.SQS_QUEUE_EVENT_COLLECTE),
					objectMapper.writeValueAsString(eventTourneeMdl));
			amazonSQS.sendMessage(sendMessageRequest);
		}catch(Exception e) {
			log.error("creationMessage : {}",messageAtlasCreationMdl,e);
		}
	}
	
	private MessageAtlasLectureMdl toMessageAtlasLectureMdl(final MessageAtlas messageAtlas, final long idtournee) {
		MessageAtlasLectureMdl messageAtlasLectureMdl = new MessageAtlasLectureMdl();
		messageAtlasLectureMdl.setDhfmtcreation(toLocalDateTimeFmt(messageAtlas.getDhsaisie()));
		messageAtlasLectureMdl.setIdmessage(messageAtlas.getId());
		messageAtlasLectureMdl.setIdtournee(idtournee);
		messageAtlasLectureMdl.setMessage(messageAtlas.getMessage());
		return messageAtlasLectureMdl;
	}
	
	@Transactional
	public void marquerCommeLu(MessageAtlasLectureMdl messageAtlasLectureMdl) {
		try {
			MessageAtlas messageAtlas = messageAtlasRepository.findById(messageAtlasLectureMdl.getIdmessage()).get();
			messageAtlas.setLecture(true);
			messageAtlas.setDhlecture(LocalDateTime.now());
			messageAtlasRepository.save(messageAtlas);
		}catch(Exception e) {
			log.error("marquerCommeLu : " + e.toString() + " avec messageAtlasLectureMdl : " + messageAtlasLectureMdl);
		}
	}

	@Transactional
	public List<MessageAtlasLectureMdl> messagesnonrecu() {
		List<MessageAtlasLectureMdl> messagesnonrecu = new ArrayList<>();
		try {
			List<MessageAtlas> messages = messageAtlasRepository.findByDhreceptionIsNull();
			for(MessageAtlas message : messages) {
				if(message.getDhsaisie().isBefore(LocalDateTime.now().minusMinutes(1))) {
					messagesnonrecu.add(toMessageAtlasLectureMdl(message, message.getTournee().getId()));
				}
			}
		}catch(Exception e) {
			log.error("messagesnonrecu",e);
		}
		return messagesnonrecu;
	}
}
