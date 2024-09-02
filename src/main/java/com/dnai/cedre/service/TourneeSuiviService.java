package com.dnai.cedre.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.PositionevtRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.dao.SignalementRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Positionevt;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.domain.Signalement;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.ClientMdl;
import com.dnai.cedre.model.MajTourneeOseMdl;
import com.dnai.cedre.model.MediaMdl;
import com.dnai.cedre.model.OperationMdl;
import com.dnai.cedre.model.PositionevtMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.model.cockpit.AvpDetailMdl;
import com.dnai.cedre.model.cockpit.AvpMdl;
import com.dnai.cedre.model.cockpit.AvpPhotoMdl;
import com.dnai.cedre.util.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TourneeSuiviService extends ParentService{

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private PrestationRepository prestationRepository;
	
	@Autowired
	private SignalementRepository signalementRepository;
	
	@Autowired
	private PositionevtRepository positionevtRepository;
	
	@Autowired
	private GeolocalisationService geolocalisationService;
	
	@Autowired
	private MediaService mediaService;
	
	@Autowired
	private AvisdePassageService avisdePassageService;
		
	@Autowired
	private AmazonSQS amazonSQS;
	
	@Autowired
	private Environment env;
	
    @Autowired
    private ObjectMapper objectMapper;
	
	@Transactional
	private boolean existenceTournee(Date datetournee, String idose, int notour) {
		boolean existenceTournee = false;
		try {
			existenceTournee = !(tourneeRepository.findFirstByDatetourneeAndNotourAndIdoseAndTypetournee(datetournee,notour,idose, Tournee.Typetournee.COLLECTE.toString())==null);
			if(existenceTournee) {
				log.error("existenceTournee : tentative de doublon pour datetournee : " + datetournee + ", idose : " + idose + ",notour : " + notour);
			}
		}catch(Exception e) {
			log.error("existenceTournee : " + e.toString());
		}
		return existenceTournee;
	}
	
	@Transactional
	public void debutClient(final ClientMdl clientMdl) {
		try {
			Collecte collecte = collecteRepository.findById(clientMdl.getIdcollecteadnai()).get();
			collecte.setDhdebut(longToDate(clientMdl.getDebut()));
			collecte.setEtat(clientMdl.getEtat());
			collecteRepository.save(collecte);
		}catch(Exception e) {
			log.error("debutClient : clientMdl : {}",clientMdl,e);
		}
	}
	
	@Transactional
	public void proximiteClient(ClientMdl clientMdl) {
		try {
			Collecte collecte = collecteRepository.findById(clientMdl.getIdcollecteadnai()).get();
			if(clientMdl.getDhproximite()==0) {
				collecte.setDhproximite(null);
			}else {
				collecte.setDhproximite(longToDate(clientMdl.getDhproximite()));
			}
			collecteRepository.save(collecte);
		}catch(Exception e) {
			log.error("debutClient : clientMdl : {}",clientMdl,e);
		}
	}
	
	@Transactional
	public void finaliseClient(ClientMdl clientMdl) {
		try {
			Collecte collecte = collecteRepository.findById(clientMdl.getIdcollecteadnai()).get();
			if(!Constantes.COLLECTE_ETAT_TRAITE.equals(collecte.getEtat()) 
					&& !Constantes.COLLECTE_ETAT_IMPOSSIBLE.equals(collecte.getEtat())) {
				Date dhdebut = longToDate(clientMdl.getDebut());
				Date dhproximite = longToDate(clientMdl.getDhproximite());
				
				collecte.setDhdebut(dhdebut);
				collecte.setDhfin(longToDate(clientMdl.getFin()));
				collecte.setDhproximite(calculDhproximite(dhdebut,dhproximite));
				collecte.setEtat(clientMdl.getEtat());
				if(clientMdl.getRecept()!=null) {
					collecte.setRecepfonction(StringUtils.abbreviate(clientMdl.getRecept().getFonction(),50));
					collecte.setReceptitre(StringUtils.abbreviate(clientMdl.getRecept().getTitre(),50));
				}
				
				if(clientMdl.getSignature()!=null && clientMdl.getSignature().getBase64media()!=null 
						&& MediaMdl.Etatsync.CLIENT.toString().equals(clientMdl.getSignature().getEtatsync())) {
					mediaService.enregistreMedia(clientMdl.getSignature());
				}
				
				collecteRepository.save(collecte);
			}
		}catch(Exception e) {
			log.error("finaliseClient : clientMdl : {}",clientMdl,e);
		}
	}
			
	Date calculDhproximite(final Date dhdebut, final Date dhproximite) {
		if(dhproximite==null) {
			return dhdebut;
		}else {
			if(dhdebut!=null && dhproximite.after(dhdebut)) {
				return dhdebut;
			}else {
				return dhproximite;
			}
		}
	}
	
	private AvpMdl construitAvpMdl(ClientMdl clientMdl, Tournee tournee) {
		AvpMdl avpMdl = new AvpMdl();
		
		String idavp = "";
		idavp += longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET);
		idavp += ".";
		if(isDevProfile()) {
			idavp += System.currentTimeMillis();
		}else {
			idavp += tournee.getIdose();
			idavp += ".";
			idavp += clientMdl.getId();
		}

		avpMdl.setIdavp(idavp);
		avpMdl.setNomClient(clientMdl.getNom());
		avpMdl.setAdresse(clientMdl.getAdr1());
		avpMdl.setCodepostal(clientMdl.getCodepostal());
		avpMdl.setVille(clientMdl.getVille());
		avpMdl.setIdtourneeose(tournee.getIdose());
		avpMdl.setIdclientose(clientMdl.getId());
		
		avpMdl.setAgents(tournee.getLibequipe());
		
		if(clientMdl.getFin()>0) {
			avpMdl.setDateAvp(longToDateTimeFmtPattern(clientMdl.getFin(),"dd/MM/yyyy"));
			avpMdl.setHeureAvp(longToDateTimeFmtPattern(clientMdl.getFin(),Constantes.PATTERN_HHMM));
		}else {
			avpMdl.setDateAvp("");
			avpMdl.setHeureAvp("");
		}

		avpMdl.setVehicule(tournee.getVehicule());
		
		if(clientMdl.getSignature()!=null && clientMdl.getSignature().getUrl()!=null) {
			avpMdl.setSignature(clientMdl.getSignature().getUrl());
			avpMdl.setSignaturePresente(true);
		}else {
			avpMdl.setSignaturePresente(false);
		}
		
		String infosSignataire = "";
		if(clientMdl.getRecept()!=null) {
			if(clientMdl.getRecept().getTitre()!=null) {
				infosSignataire += clientMdl.getRecept().getTitre();
				if(clientMdl.getRecept().getFonction()!=null) {
					infosSignataire += " - ";
					infosSignataire += clientMdl.getRecept().getFonction();
				}
			}
		}
		avpMdl.setInfosSignataire(infosSignataire);
		
		for(OperationMdl operationMdl : clientMdl.getOperations()) {
			AvpDetailMdl avpDetailMdl = new AvpDetailMdl();
			avpDetailMdl.setOperation(operationMdl.getLibelle());
			avpDetailMdl.setDispositif(operationMdl.getNom());
			avpDetailMdl.setQteprev(operationMdl.getQteprev());
			avpDetailMdl.setQtereal(operationMdl.getQtereel());
			avpMdl.getDetails().add(avpDetailMdl);
		}
		
		//avpMdl.setPhotosPresentes(!clientMdl.getAvpimage().isEmpty());
		for(MediaMdl mediaMdl : clientMdl.getAvpimage()) {
			if(!Strings.isBlank(mediaMdl.getUrl()) && !mediaMdl.getUrl().endsWith(".heic")) {
				AvpPhotoMdl avpPhotoMdl = new AvpPhotoMdl();
				avpPhotoMdl.setUrl(mediaMdl.getUrl());
				avpPhotoMdl.setDhfmt(longToDateTimeFmtPattern(mediaMdl.getDateheure(), Constantes.PATTERN_DATEHEURE));
				avpMdl.getPhotos().add(avpPhotoMdl);
			}
		}
		avpMdl.setPhotosPresentes(!avpMdl.getPhotos().isEmpty());
		
		return avpMdl;
	}
	
	@Transactional
	private void checkMedia(final ClientMdl clientMdl) {
		try {
			// avpimage
			for(MediaMdl mediaMdl : clientMdl.getAvpimage()) {
				if(mediaMdl.getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
					mediaMdl = mediaService.enregistreMedia(mediaMdl);
				}
			}
			
			// sigltimage
			for(MediaMdl mediaMdl : clientMdl.getSigltimage()) {
				if(mediaMdl.getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
					mediaMdl = mediaService.enregistreMedia(mediaMdl);
				}
			}
			
			// sigltaudio
			for(MediaMdl mediaMdl : clientMdl.getSigltaudio()) {
				if(mediaMdl.getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
					mediaMdl = mediaService.enregistreMedia(mediaMdl);
				}
			}
		}catch(Exception e) {
			log.error("checkMedia : {clientMdl}",clientMdl,e);
		}
	}
	
	@Transactional
	public TourneeInfoMdl finaliseTournee(final TourneeInfoMdl tourneeInfoMdl, final String cleServiceOse) {
		try {
			Tournee tournee = tourneeRepository.findById(tourneeInfoMdl.getIdadnai()).get();

			tournee.setCleflux(cleServiceOse);
			tournee.setEcart(isEcart(tourneeInfoMdl));
			tournee.setSignt(isSignt(tourneeInfoMdl));
			
			tournee.setDhdebut(longToDate(tourneeInfoMdl.getDebut()));
			tournee.setDhfin(longToDate(tourneeInfoMdl.getFin()));
			tournee.setEtat(Constantes.TOURNEE_ETAT_FIN);
			tournee.setDhcentredepart(longToDate(tourneeInfoMdl.getDhcentredepart()));
			tournee.setDhcentreretour(longToDate(tourneeInfoMdl.getDhcentreretour()));
			
			Map<String, Long> mapDistance = mapDistance(tourneeInfoMdl.getPositionsevt());
			tournee.setDistance(mapDistance.get("total"));
			
			for(PositionevtMdl positionevtMdl : tourneeInfoMdl.getPositionsevt()) {
				if(positionevtRepository.findFirstByTourneeAndEvt(tournee, positionevtMdl.getEvt())==null) {
					Positionevt positionevt = new Positionevt();
					positionevt.setTournee(tournee);
					positionevt.setDhevt(longToDate(positionevtMdl.getDhevt()));
					positionevt.setEvt(positionevtMdl.getEvt());
					positionevt.setLatitude(positionevtMdl.getLatitude());
					positionevt.setLongitude(positionevtMdl.getLongitude());
					positionevt.setCategorie(positionevtMdl.getCategorie());
					positionevtRepository.save(positionevt);
				}
			}
			
			for(ClientMdl clientMdl : tourneeInfoMdl.getClients()) {
				finaliseClient(clientMdl);
				Collecte collecte = collecteRepository.findById(clientMdl.getIdcollecteadnai()).get();
				collecte.setDistance(mapDistance.get(clientMdl.getId()));
				
				checkMedia(clientMdl);
				
				// avis de passage
				AvpMdl avpMdl = construitAvpMdl(clientMdl, tournee);
				String urlAvp = avisdePassageService.genereEtStockeAvp(avpMdl);
				collecte.setUrlavp(urlAvp);
				clientMdl.setUrlavp(urlAvp);
				
				collecteRepository.save(collecte);
								
				for(OperationMdl operationMdl : clientMdl.getOperations()) {
					String libelle = contruitLibPrestation(operationMdl.getLibelle(), operationMdl.getNom());
					Prestation prestation = prestationRepository.findById(operationMdl.getIdprestationadnai()).orElse(null);
					if(prestation==null) {
						prestation = new Prestation();
						prestation.setCollecte(collecte);
						prestation.setLibelle(libelle);
					}
					prestation.setQteabsent(operationMdl.getBacabsent());
					prestation.setQteprevu(operationMdl.getQteprev());
					prestation.setQtereel(operationMdl.getQtereel());
					prestation.setQtevide(operationMdl.getBacvide());
					double poids = operationMdl.getPdsreel();
					prestation.setPoids(poids);
					prestation.setPoidsparagent(clientMdl.getNbagents()>0?poids/clientMdl.getNbagents():0);
					prestationRepository.save(prestation);
				}
				
				if(clientMdl.getSiglttext()!=null && !clientMdl.getSiglttext().trim().isEmpty()) {
					Date datecreation = new Date(clientMdl.getFin());
					if(signalementRepository.findFirstByCollecteAndDatecreation(collecte, datecreation)==null) {
						Signalement signalement = new Signalement();
						signalement.setCollecte(collecte);
						signalement.setDatecreation(datecreation);
						signalement.setTexte(clientMdl.getSiglttext());
						signalement.setTypesignt(Signalement.Typesignt.TEXTE.toString());
						signalementRepository.save(signalement);
					}else {
						log.error("finaliseTournee : signalement existant pour collecte : " + collecte + " et datecreation : " + datecreation);
					}
				}
								
				for(MediaMdl mediaMdl : clientMdl.getSigltaudio()) {
					Date datecreation = new Date(clientMdl.getFin());
					if(signalementRepository.findFirstByCollecteAndDatecreation(collecte, datecreation)==null) {
						Signalement signalement = new Signalement();
						signalement.setCollecte(collecte);
						signalement.setDatecreation(new Date(mediaMdl.getDateheure()));
						signalement.setTypesignt(Signalement.Typesignt.AUDIO.toString());
						signalement.setUrl(mediaMdl.getUrl());
						signalementRepository.save(signalement);
					}else {
						log.error("finaliseTournee : signalement existant pour collecte : " + collecte + " et datecreation : " + datecreation);
					}
				}
				
				for(MediaMdl mediaMdl : clientMdl.getSigltimage()) {
					Date datecreation = new Date(clientMdl.getFin());
					if(signalementRepository.findFirstByCollecteAndDatecreation(collecte, datecreation)==null) {
						Signalement signalement = new Signalement();
						signalement.setCollecte(collecte);
						signalement.setDatecreation(new Date(mediaMdl.getDateheure()));
						signalement.setTypesignt(Signalement.Typesignt.IMAGE.toString());
						signalement.setUrl(mediaMdl.getUrl());
						signalementRepository.save(signalement);
					}else {
						log.error("finaliseTournee : signalement existant pour collecte : " + collecte + " et datecreation : " + datecreation);
					}
				}
			}
			
			MajTourneeOseMdl majTourneeOseMdl = new MajTourneeOseMdl();
			majTourneeOseMdl.setId(tournee.getId());
			SendMessageRequest sendMessageRequest = new SendMessageRequest(env.getProperty(Constantes.SQS_QUEUE_MAJTOURNEEOSE),
					objectMapper.writeValueAsString(majTourneeOseMdl));
			String msgid = UUID.randomUUID().toString();
			sendMessageRequest.setMessageGroupId(msgid);
			sendMessageRequest.setMessageDeduplicationId(msgid);
			amazonSQS.sendMessage(sendMessageRequest);
			
		}catch(Exception e) {
			logJsonTournee(tourneeInfoMdl);
			log.error("finaliseTournee : tourneeInfoMdl : {}",tourneeInfoMdl,e);
		}
		return tourneeInfoMdl;
	}
		
	private boolean isEcart(TourneeInfoMdl tourneeInfoMdl) {
		boolean isEcart = false;
		try {
			// Anomalie = Bac Absent ou Qté réelle > Qté prévue
			for(ClientMdl clientMdl : tourneeInfoMdl.getClients()) {
				for(OperationMdl operationMdl : clientMdl.getOperations()) {
					if(operationMdl.getBacabsent()>0) {
						isEcart = true;
						break;
					}
					int qteprev = parseInt(operationMdl.getQteprev());
					int qtereel = parseInt(operationMdl.getQtereel());
					if(qtereel>qteprev) {
						isEcart = true;
						break;
					}
				}
			}
		}catch(Exception e) {
			log.error("isEcart : " + e.toString());
		}
		return isEcart;
	}
	
	private boolean isSignt(TourneeInfoMdl tourneeInfoMdl) {
		boolean isSignt = false;
		try {

			for(ClientMdl clientMdl : tourneeInfoMdl.getClients()) {
				
				if(clientMdl.getSiglttext()!=null && !clientMdl.getSiglttext().trim().isEmpty()) {
					isSignt = true;
					break;
				}
				if(!clientMdl.getSigltaudio().isEmpty() || !clientMdl.getSigltimage().isEmpty()) {
					isSignt = true;
					break;
				}
			}
		}catch(Exception e) {
			log.error("isSignt : " + e.toString());
		}
		return isSignt;
	}
	
	Map<String, Long> mapDistance(List<PositionevtMdl> positionsevt){
		Map<String, Long> mapDistance = new HashMap<>();
		
		try {
			long distanceTotale = 0;
			String origin = null;
			String destination = null;
			String idclientose = null;
			for(PositionevtMdl positionevtMdl : positionsevt) {
				if("DEBUT_TOURNEE".equals(positionevtMdl.getEvt())){
					origin = positionevtMdl.getLatitude() + "," + positionevtMdl.getLongitude();
				}
				//if(positionevtMdl.getEvt().startsWith("FIN_CLIENT")) {
				if(positionevtMdl.getEvt().startsWith("DEBUT_CLIENT")) {
					destination = positionevtMdl.getLatitude() + "," + positionevtMdl.getLongitude();
					idclientose = positionevtMdl.getEvt().split(":")[1];
				}
				if(origin!=null && destination!=null && idclientose!=null) {
					//long distanceClient = geolocalisationService.calculDistance(origin, destination);
					long distanceClient = 0;
					distanceTotale = distanceTotale + distanceClient;
					mapDistance.put(idclientose, distanceClient);
					origin = destination;
					//destination = null;
					idclientose = null;
				}
				if("FIN_TOURNEE".equals(positionevtMdl.getEvt())){
					destination = positionevtMdl.getLatitude() + "," + positionevtMdl.getLongitude();
					//long distanceRetour = geolocalisationService.calculDistance(origin, destination);
					long distanceRetour = 0;
					distanceTotale = distanceTotale + distanceRetour;
				}
			}
			mapDistance.put("total", distanceTotale);
		}catch(Exception e) {
			log.error("mapDistance : " + e.toString());
		}
		return mapDistance;
	}
}
