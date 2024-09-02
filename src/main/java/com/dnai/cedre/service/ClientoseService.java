package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.ClientoseRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.model.cockpit.ClientoseCritereMdl;
import com.dnai.cedre.model.cockpit.ClientoseMdl;

@Service
public class ClientoseService extends ParentService{

	@Autowired
	private ClientoseRepository clientoseRepository;
		
	@Transactional
	public List<ClientoseMdl> clientsose(ClientoseCritereMdl clientoseCritereMdl){
		List<ClientoseMdl> clientsosemdl = new ArrayList<>();
		
		if(clientoseCritereMdl.getIdutilisateur()==0) {
			List<Clientose> clientsose =  clientoseRepository.findAllByOrderByNomAsc();
			for(Clientose clientose : clientsose) {
				ClientoseMdl clientoseMdl = new ClientoseMdl(clientose.getId(),clientose.getNom());
				clientsosemdl.add(clientoseMdl);
			}
		}else {
			Map<Long,Clientose> clientOsePerimetre = clientOseParPerimetre(clientoseCritereMdl.getIdutilisateur(), clientoseCritereMdl.getIdgroupesite());
			List<Clientose> clientsose = new ArrayList<>(clientOsePerimetre.values());
			for(Clientose clientose : clientsose) {
				ClientoseMdl clientoseMdl = new ClientoseMdl(clientose.getId(),clientose.getNom());
				clientsosemdl.add(clientoseMdl);
			}
			
			/*
			List<UtilisateurGroupesite> listUtilisateurGroupesite =  utilisateurGroupesiteRepository.findByIdutilisateur(clientoseCritereMdl.getIdutilisateur());
			Map<Long,ClientoseMdl> mapClientose = new HashMap<>();
			for(UtilisateurGroupesite utilisateurGroupesite : listUtilisateurGroupesite) {
				long idgroupesite = utilisateurGroupesite.getIdgroupesite();
				if(clientoseCritereMdl.getIdgroupesite()==0 || (clientoseCritereMdl.getIdgroupesite()==idgroupesite)) {
					List<GroupesiteClientose> listGroupesiteClientose = groupesiteClientoseRepository.findByIdgroupesite(idgroupesite);
					for(GroupesiteClientose groupesiteClientose : listGroupesiteClientose) {
						Clientose clientose = clientoseRepository.findById(groupesiteClientose.getIdclientose()).get();
						ClientoseMdl clientoseMdl = new ClientoseMdl(clientose.getId(),clientose.getNom());
						mapClientose.put(clientoseMdl.getId(), clientoseMdl);
					}
				}
			}
			clientsosemdl.addAll(mapClientose.values());
			*/
			Collections.sort(clientsosemdl);
		}

		
		return clientsosemdl;
	}
	
}
