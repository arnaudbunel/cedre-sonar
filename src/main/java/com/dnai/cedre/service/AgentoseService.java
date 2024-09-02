package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.AgentoseRepository;
import com.dnai.cedre.domain.Agentose;
import com.dnai.cedre.model.cockpit.AgentoseMdl;

@Service
public class AgentoseService extends ParentService{
	
	@Autowired
	private AgentoseRepository agentoseRepository;
	
	@Transactional
	public List<AgentoseMdl> agentsose(){
		List<AgentoseMdl> agentsosemdl = new ArrayList<>();
		
		boolean isDev = isDevProfile();
		
		List<Agentose> agentsose =  agentoseRepository.findAllByOrderByNomAsc();
		for(Agentose agentose : agentsose) {
			if(isDev || !agentose.getNom().startsWith("ADNAI")) {
				AgentoseMdl agentoseMdl = new AgentoseMdl(agentose.getId(),agentose.getNom());
				agentsosemdl.add(agentoseMdl);
			}
		}
		return agentsosemdl;
	}
}
