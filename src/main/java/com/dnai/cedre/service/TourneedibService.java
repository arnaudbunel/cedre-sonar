package com.dnai.cedre.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dnai.cedre.dao.AgentoseRepository;
import com.dnai.cedre.dao.CollecteDibRepository;
import com.dnai.cedre.dao.PrestationDibRepository;
import com.dnai.cedre.dao.TourneeAgentRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.dao.TranscoSignalementRepository;
import com.dnai.cedre.domain.Agentose;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.CollecteDib;
import com.dnai.cedre.domain.PrestationDib;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.domain.TourneeAgent;
import com.dnai.cedre.domain.TranscoSignalement;
import com.dnai.cedre.model.AdresseMdl;
import com.dnai.cedre.model.ClientMdl;
import com.dnai.cedre.model.MajTourneeOseMdl;
import com.dnai.cedre.model.SignalementMdl;
import com.dnai.cedre.model.dib.ClientdibMdl;
import com.dnai.cedre.model.dib.OperationdibMdl;
import com.dnai.cedre.model.dib.TourneedibMdl;
import com.dnai.cedre.model.ose.AgtMdl;
import com.dnai.cedre.model.ose.DispoMdl;
import com.dnai.cedre.model.ose.OpMdl;
import com.dnai.cedre.model.ose.PcMdl;
import com.dnai.cedre.model.ose.SceTourneeMdl;
import com.dnai.cedre.util.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TourneedibService extends ParentService{
	
	@Autowired
	private OseService oseService;
	
	@Autowired
	private TourneeUtilService tourneeUtilService;
	
	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private TourneeAgentRepository tourneeAgentRepository;
	
	@Autowired
	private AgentoseRepository agentoseRepository;
	
	@Autowired
	private CollecteDibRepository collecteDibRepository;
	
	@Autowired
	private PrestationDibRepository prestationDibRepository;
	
	@Autowired
	private TranscoSignalementRepository transcoSignalementRepository;
	
	@Autowired
	private AmazonSQS amazonSQS;
	
	@Autowired
	private Environment env;
	
    @Autowired
    private ObjectMapper objectMapper;
	
	public TourneedibMdl majTourneeDib(final TourneedibMdl tourneedibMdl) {
		try {
			Tournee tournee = tourneeRepository.findById(tourneedibMdl.getIdadnai()).get();
			tournee.setEtat(tourneedibMdl.getEtat());
			tournee.setPoidsdib(tourneedibMdl.getPoidsdib());
			
			if(tourneedibMdl.getDhpesee()>0) {
		        LocalDateTime dhpesee =
		        	       LocalDateTime.ofInstant(Instant.ofEpochMilli(tourneedibMdl.getDhpesee()),
		        	                               TimeZone.getDefault().toZoneId());
		        tournee.setDhpesee(dhpesee);
			}
			
			tournee.setDhfin(longToDate(tourneedibMdl.getFin()));
			
			tourneeRepository.save(tournee);
			
			
			// envoi message sqs pour majose
			if(Tournee.Etat.FIN.toString().equals(tournee.getEtat())){
				MajTourneeOseMdl majTourneeOseMdl = new MajTourneeOseMdl();
				majTourneeOseMdl.setId(tournee.getId());
				SendMessageRequest sendMessageRequest = new SendMessageRequest(env.getProperty(Constantes.SQS_QUEUE_MAJTOURNEEOSE),
						objectMapper.writeValueAsString(majTourneeOseMdl));
				String msgid = UUID.randomUUID().toString();
				sendMessageRequest.setMessageGroupId(msgid);
				sendMessageRequest.setMessageDeduplicationId(msgid);
				amazonSQS.sendMessage(sendMessageRequest);
			}
			
		}catch(Exception e) {
			log.error("majTourneeDib",e);
		}
		return tourneedibMdl;
	}
	
	public TourneedibMdl construitTourneeDib(SceTourneeMdl sceTournee, String codeservice/*, String token*/, final String cleflux) {
		TourneedibMdl tourneedibMdl = null;
		try {
			tourneedibMdl = construitTourneedibInfo(sceTournee, codeservice/*, token*/);
			tourneedibMdl = creerTournee(tourneedibMdl, sceTournee, cleflux);
		}catch(Exception e) {
			log.error("construitTourneeDib",e);
		}
		return tourneedibMdl;
	}

	public TourneedibMdl rechercheTourneeDib(final String codeservice) {
		TourneedibMdl tourneedibMdl = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -5);
			Tournee tournee = tourneeRepository.findFirstByTypetourneeAndCodeserviceIgnoreCaseAndEtatAndDatetourneeAfter(Tournee.Typetournee.DIB.toString(), codeservice, Tournee.Etat.PESEE.toString(), cal.getTime());
			if(tournee!=null) {
				tourneedibMdl = new TourneedibMdl();
				tourneedibMdl.setDatefmt(longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),"yyyy-MM-dd"));
				tourneedibMdl.setIdadnai(tournee.getId());
				tourneedibMdl.setEtat(tournee.getEtat());
			}
		}catch(Exception e) {
			log.error("rechercheTourneeDib",e);
		}
		return tourneedibMdl;
	}
	
	private TourneedibMdl construitTourneedibInfo(final SceTourneeMdl sceTournee, final String codeservice/*, final String token*/) {
		TourneedibMdl tourneeInfoMdl = new TourneedibMdl();
		tourneeInfoMdl.setDatefmt(sceTournee.getDate());
		tourneeInfoMdl.setIdentifiant(sceTournee.getId());
		tourneeInfoMdl.setTour(sceTournee.getTour());
		tourneeInfoMdl.setVehicule(oseService.extraitVehicule(sceTournee));
		tourneeInfoMdl.setCodeservice(codeservice.toUpperCase());
		// tourneeInfoMdl.setToken(token);
		
		int i=1;
		if(sceTournee.getDessertes()!=null && sceTournee.getDessertes().getPc()!=null) {
			for(PcMdl pc : sceTournee.getDessertes().getPc()) {
				ClientdibMdl client = construitClientdibMdl(pc, i);			
				tourneeInfoMdl.getClients().add(client);
				i++;
			}
		}else {
			log.warn("construitTourneedibInfo : pc null pour sceTournee : " + sceTournee);
		}
		
		return tourneeInfoMdl;
	}
	
	private ClientdibMdl construitClientdibMdl(final PcMdl pc, final int i/*, final String idtournee, final String tour, final String tourorigine*/) {
		AdresseMdl adresseMdl = oseService.construitAdresse(pc.getLoc());

		ClientdibMdl client = new ClientdibMdl();
		client.setId(pc.getId());
		client.setEtat(ClientMdl.Etat.ATTENTE.toString());
		client.setNom(pc.getNom());
		client.setAdr1(adresseMdl.getAdr1());
		client.setAdr2(adresseMdl.getAdr2());
		client.setCodepostal(adresseMdl.getCodepostal());
		client.setHoraires(pc.getConsignes().getHoraires());
		client.setVille(adresseMdl.getVille());
		client.setAdressefmt(adresseMdl.getLibadr()!=null?adresseMdl.getLibadr().replace(", FR", ""):"");
		client.setNumero(i);
		client.setMandant(pc.getMandant()!=null?pc.getMandant():"0");
		
		// signalements
		List<TranscoSignalement> signalements = transcoSignalementRepository.findByIdmandantAndActif(parseInt(pc.getMandant()), true);
		for(TranscoSignalement transcoSignalement : signalements) {
			SignalementMdl signalementMdl = new SignalementMdl();
			signalementMdl.setIdose(transcoSignalement.getIdcedre());
			signalementMdl.setLibelle(transcoSignalement.getTitre());
			client.getSignalements().add(signalementMdl);
		}
		
		for(OpMdl op : pc.getOperations().getOp()) {
			if(op.getDispo()!=null && !op.getDispo().isEmpty()) {
				for(DispoMdl dispo : op.getDispo()) {
					OperationdibMdl operationMdl = new OperationdibMdl();
					operationMdl.setId(op.getId());
					operationMdl.setDispoid(dispo.getId()!=null?dispo.getId():"0");
					operationMdl.setLibelle(op.getNom());
					operationMdl.setNom(dispo.getNom());
					operationMdl.setType(op.getType());
					operationMdl.setDechet(dispo.getDechet()!=null?dispo.getDechet():"");
					operationMdl.setQteprev(traitementQteOseToNum(dispo.getQteprev()));
					operationMdl.setQtereel(traitementQteOseToNum(dispo.getQteprev()));
					operationMdl.setCodetype(tourneeUtilService.calculCodetype(op));
					operationMdl.setCleose(tourneeUtilService.genereCleOseOperation(op,dispo));
					operationMdl.setCompo(op.getCompo());
					client.getOperations().add(operationMdl);
				}
			}
		}
		
		return client;
	}
	
	@Transactional
	public TourneedibMdl creerTournee(TourneedibMdl tourneedibMdl, SceTourneeMdl sceTourneeMdl, final String cleflux) {
		try {
			//SceTourneeMdl sceTourneeMdl = tourneeUtilService.extraitTourCourant(getSceResponseMdl, tourneedibMdl.getIdentifiant(), tourneedibMdl.getTour());
			//int nombretour = nombreTour(getSceResponseMdl);
			int nombretour = 1;
			
			int notour = parseInt(tourneedibMdl.getTour());
			Date datetournee = tourneeUtilService.extraitDateTournee(tourneedibMdl.getDatefmt());
			
			if(sceTourneeMdl!=null) {
				boolean isTourneeExistante = true;
				Tournee tournee = tourneeRepository.findFirstByDatetourneeAndNotourAndIdoseAndTypetournee(datetournee,notour,tourneedibMdl.getIdentifiant(), Tournee.Typetournee.DIB.toString());
				if(tournee==null || isDevProfile() || tourneedibMdl.getIdentifiant().startsWith(Constantes.PREFIX_TOURNEE_ADNAITEST)) {
					Tournee tourneeNew = new Tournee();
					tourneeNew.setDhdebut(new Date());
					tourneeNew.setDatetournee(datetournee);
					tourneeNew.setVehicule(tourneedibMdl.getVehicule());
					tourneeNew.setNotour(notour);
					tourneeNew.setNombretour(nombretour);
					tourneeNew.setIdose(tourneedibMdl.getIdentifiant());
					tourneeNew.setLibequipe(tourneeUtilService.extraitLibequipe(sceTourneeMdl));
					tourneeNew.setIdcentre(tourneeUtilService.extraitCentreId(sceTourneeMdl));
					tourneeNew.setEtat(Constantes.TOURNEE_ETAT_DEBUT);
					tourneeNew.setCodeservice(tourneedibMdl.getCodeservice());
					tourneeNew.setToken(tourneedibMdl.getToken());
					tourneeNew.setTypetournee(Tournee.Typetournee.DIB.toString());
					tourneeNew.setCleflux(cleflux);
					tournee = tourneeRepository.save(tourneeNew);
					isTourneeExistante = false;
				}else {
					tournee.setDhdebut(new Date());
					tournee.setToken(tourneedibMdl.getToken());
					tourneeRepository.save(tournee);
					log.warn("creerTournee : tournee existante pour tourneeInfoMdl : " + tourneedibMdl);
				}
				tourneedibMdl.setDebut(tournee.getDhdebut().getTime());
				tourneedibMdl.setIdadnai(tournee.getId());
				
				// Agents
				List<AgtMdl> agents = sceTourneeMdl.getRessources().getAgts().getAgt();
				int nbAgents = agents.size();
				//tourneedibMdl.setNbagents(nbAgents);
				
				if(!isTourneeExistante) {
					for(AgtMdl agtMdl : agents) {
						Agentose agentose = agentoseRepository.findFirstByIdose(agtMdl.getId());
						if(agentose==null) {
							Agentose agentosenew = new Agentose();
							agentosenew.setIdose(agtMdl.getId());
							agentosenew.setNom(agtMdl.getNom());
							agentose = agentoseRepository.save(agentosenew);
						}
						tourneeAgentRepository.save(new TourneeAgent(tournee.getId(),agentose.getId()));
					}
				}
				
				for(ClientdibMdl clientMdl : tourneedibMdl.getClients()) {
					clientMdl.setIdtourneeadnai(tournee.getId());
					
					Clientose clientose = creerClientose(clientMdl);
					
					CollecteDib collecte = creerCollecteDib(isTourneeExistante, tournee, clientose, clientMdl);

					clientMdl.setIdcollecteadnai(collecte.getId());
					clientMdl.setNbagents(nbAgents);
				}
				
				/*Centre centre = centreRepository.findById(Integer.valueOf(tournee.getIdcentre()).longValue()).get();
				if(centre!=null) {
					CentreMdl centreMdl = new CentreMdl();
					centreMdl.setLatitude(centre.getLatitude());
					centreMdl.setLongitude(centre.getLongitude());
					centreMdl.setNom(centre.getNom());
					tourneeInfoMdl.setCentre(centreMdl);
				}else {
					log.error("creerTournee : centre null pour idcentre : "  + tournee.getIdcentre());
				}*/
			}else {
				log.error("sceTourneeMdl null pour tourneedibMdl : {}",tourneedibMdl);
			}
			
		}catch(Exception e) {
			log.error("creerTournee : tourneeInfoMdl : {}, sceTourneeMdl : {}",tourneedibMdl,sceTourneeMdl,e);
		}
		return tourneedibMdl;
	}
	
	@Transactional
	private CollecteDib creerCollecteDib(final boolean isTourneeExistante, final Tournee tournee, final Clientose clientose, final ClientdibMdl clientMdl) {
		CollecteDib collecteDib = null;
		try {
			List<OperationdibMdl> listOperationMdlMaj = new ArrayList<>();
			if(isTourneeExistante) {
				collecteDib = collecteDibRepository.findFirstByTourneeAndClientose(tournee, clientose);
			}
			
			if(collecteDib!=null) {

				int position = 0;
				for(OperationdibMdl operationMdl : clientMdl.getOperations()) {
					PrestationDib prestationDib = prestationDibRepository.findFirstByCollectedibAndPosition(collecteDib, position);							
					if(prestationDib==null) {
						prestationDib = creerPrestationDib(operationMdl, collecteDib, position);
					}
					if(prestationDib!=null) {
						operationMdl.setIdprestationadnai(prestationDib.getId());
					}
					listOperationMdlMaj.add(operationMdl);
					position++;
				}
			}else {
				CollecteDib collecteDibNew = new CollecteDib();
				collecteDibNew.setClientose(clientose);
				collecteDibNew.setTournee(tournee);
				collecteDibNew.setEtat(Constantes.COLLECTE_ETAT_ATTENTE);
				collecteDibNew.setMandant(Integer.valueOf(clientMdl.getMandant()));
				collecteDib = collecteDibRepository.save(collecteDibNew);
				
				int position = 0;
				for(OperationdibMdl operationMdl : clientMdl.getOperations()) {							
					PrestationDib prestationDib = creerPrestationDib(operationMdl, collecteDib, position);
					operationMdl.setIdprestationadnai(prestationDib.getId());
					listOperationMdlMaj.add(operationMdl);
					position++;
				}
				clientMdl.setOperations(listOperationMdlMaj);
			}
		}catch(Exception e) {
			log.error("creerCollecteDib",e);
		}
		return collecteDib;
	}
	
	@Transactional
	private PrestationDib creerPrestationDib(final OperationdibMdl operationMdl, final CollecteDib collecteDib, final int position) {
		PrestationDib prestationDib = null;
		try {
			PrestationDib	prestationDibNew = new PrestationDib();
			prestationDibNew.setCollectedib(collecteDib);
			String libelle = contruitLibPrestation(operationMdl.getLibelle(), operationMdl.getNom());
			prestationDibNew.setLibelle(StringUtils.abbreviate(libelle,100));
			prestationDibNew.setQteabsent(operationMdl.getQteabsent());
			prestationDibNew.setQteprevu(operationMdl.getQteprev());
			prestationDibNew.setQtereel(operationMdl.getQtereel());
			prestationDibNew.setQtevide(operationMdl.getQtevide());
			prestationDibNew.setQtedebord(operationMdl.getQtedebord());
			prestationDibNew.setQtedeclassement(operationMdl.getQtedeclassement());
			prestationDibNew.setPosition(position);
			prestationDibNew.setOperation(StringUtils.abbreviate(operationMdl.getLibelle(),50));
			prestationDibNew.setDispositif(StringUtils.abbreviate(operationMdl.getNom(),50));
			prestationDibNew.setOpid(operationMdl.getId());
			prestationDibNew.setDispoid(operationMdl.getDispoid());
			prestationDibNew.setCompo(operationMdl.getCompo());
			prestationDibNew.setCleose(operationMdl.getCleose());
			prestationDib =  prestationDibRepository.save(prestationDibNew);
		}catch(Exception e) {
			log.error("creerPrestationDib : ",e);
		}
		return prestationDib;
	}
}
