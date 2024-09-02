package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.MediaRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.dao.TourneeAgentRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Media;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.model.ClientMdl;
import com.dnai.cedre.model.ContactMdl;
import com.dnai.cedre.model.MediaMdl;
import com.dnai.cedre.model.OperationMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.model.cockpit.AvpDetailMdl;
import com.dnai.cedre.model.cockpit.AvpMdl;
import com.dnai.cedre.model.cockpit.AvpPhotoMdl;
import com.dnai.cedre.model.cockpit.CollecteMdl;
import com.dnai.cedre.model.cockpit.PrestationEncoursMdl;
import com.dnai.cedre.model.cockpit.TourneeCritereMdl;
import com.dnai.cedre.model.cockpit.TourneeMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TourneeencoursService extends ParentService{

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private PrestationRepository prestationRepository;
	
	@Autowired
	private TourneeAgentRepository tourneeAgentRepository;
	
	@Autowired
	private AvisdePassageService avisdePassageService;
	
	@Autowired
	private MediaRepository mediaRepository;
	
	@Autowired
	private OseService majOseService;
	
	@Autowired
	private DeroulementService deroulementService;
	
	@Transactional
	public List<TourneeMdl> tourneeencours(final TourneeCritereMdl tourneeCritereMdl) {
		List<TourneeMdl> tourneesencours = new ArrayList<>();
		try {
			List<Tournee> tournees = new ArrayList<>();
			Date datetournee = parseDate(tourneeCritereMdl.getDhtournee(),Constantes.PATTERN_DATETIRET);
			if(Strings.isBlank(tourneeCritereMdl.getCodeservice())) {
				tournees = tourneeRepository.findByDatetourneeAndEtatNotAndTypetournee(datetournee, Constantes.TOURNEE_ETAT_FIN, Tournee.Typetournee.COLLECTE.toString());
			}else {
				tournees = tourneeRepository.findByDatetourneeAndCodeserviceIgnoreCaseAndEtatNotAndTypetournee(datetournee, tourneeCritereMdl.getCodeservice(), Constantes.TOURNEE_ETAT_FIN, Tournee.Typetournee.COLLECTE.toString());
			}
			
			for(Tournee tournee : tournees) {
				TourneeMdl tourneeMdl = new TourneeMdl();
				tourneeMdl.setAgents(tournee.getLibequipe());
				tourneeMdl.setHdebut(longToDateTimeFmtPattern(tournee.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS));
				tourneeMdl.setIdtournee(tournee.getId());
				tourneeMdl.setTour(tournee.getNotour());
				tourneeMdl.setCodeservice(tournee.getCodeservice());
				tourneeMdl.setVehicule(tournee.getVehicule());
				
				List<Collecte> collectes = collecteRepository.findByTournee(tournee);
				tourneeMdl.setLibdetail("1 à " + collectes.size());
				
				for(Collecte collecte : collectes) {
					CollecteMdl collecteMdl = new CollecteMdl();
					collecteMdl.setIdcollecte(collecte.getId());
					Clientose clientose = collecte.getClientose();
					collecteMdl.setClient(clientose.getNom());
					collecteMdl.setIdclientose(clientose.getIdose());
					collecteMdl.setEtat(collecte.getEtat());
					collecteMdl.setHdebut(collecte.getDhdebut()!=null?longToDateTimeFmtPattern(collecte.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):null);
					collecteMdl.setHfin(collecte.getDhfin()!=null?longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):null);
					
					List<Prestation> prestations = prestationRepository.findByCollecte(collecte);
					for(Prestation prestation : prestations) {
						PrestationEncoursMdl prestationEncoursMdl = new PrestationEncoursMdl();
						prestationEncoursMdl.setIdprestation(prestation.getId());
						prestationEncoursMdl.setLibelle(prestation.getLibelle());
						prestationEncoursMdl.setQteabsent(prestation.getQteabsent());
						prestationEncoursMdl.setQteprevue(parseInt(prestation.getQteprevu()));
						prestationEncoursMdl.setQtereel(parseInt(prestation.getQtereel()));
						prestationEncoursMdl.setQtevide(prestation.getQtevide());
						prestationEncoursMdl.setIdcollecte(collecte.getId());
						prestationEncoursMdl.setNbagents(calculNbAgents(tournee));
						prestationEncoursMdl.setDispositif(prestation.getDispositif());
						prestationEncoursMdl.setOperation(prestation.getOperation());
						collecteMdl.getPrestations().add(prestationEncoursMdl);
					}
					tourneeMdl.getCollectes().add(collecteMdl);
				}
				
				tourneesencours.add(tourneeMdl);
			}
		}catch(Exception e) {
			log.error("tourneeencours {}",tourneeCritereMdl,e);
		}
		return tourneesencours;
	}

	@Transactional
	private int calculNbAgents(final Tournee tournee) {
		int nbagents = 0;
		try {
			nbagents = tourneeAgentRepository.findByIdtournee(tournee.getId()).size();
		}catch(Exception e) {
			log.error("calculNbAgents {}",tournee,e);
		}
		return nbagents;
	}
	
	private Date calculDateDefaut(final Date datetournee) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(datetournee);
		cal.set(Calendar.HOUR_OF_DAY, 20);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	@Transactional
	public CollecteMdl savecollecteencours(final CollecteMdl collecteMdl) {
		CollecteMdl collecteMdlMaj = collecteMdl;
		try {
			Collecte collecte = collecteRepository.findById(collecteMdl.getIdcollecte()).get();
			collecte.setEtat(collecteMdl.getEtat());
			
			Tournee tournee = collecte.getTournee();
			Date datedefaut = calculDateDefaut(tournee.getDatetournee());
			
			if(collecte.getDhdebut()==null) {
				collecte.setDhdebut(datedefaut);
			}
			
			if(Constantes.COLLECTE_ETAT_IMPOSSIBLE.equals(collecteMdl.getEtat())) {
				collecte.setDhfin(collecte.getDhdebut());
			}else if(Constantes.COLLECTE_ETAT_TRAITE.equals(collecteMdl.getEtat())){
				collecte.setDhfin(datedefaut);
			}
			
			for(PrestationEncoursMdl prestationEncoursMdl : collecteMdl.getPrestations()) {
				Prestation prestation = prestationRepository.findById(prestationEncoursMdl.getIdprestation()).get();
				prestation.setQteabsent(prestationEncoursMdl.getQteabsent());
				prestation.setQtereel(String.valueOf(prestationEncoursMdl.getQtereel()));
				prestation.setQtevide(prestationEncoursMdl.getQtevide());
				prestation.setPoids(prestationEncoursMdl.getPoids());
				prestation.setPoidsparagent(prestationEncoursMdl.getNbagents()>0?prestationEncoursMdl.getPoids()/prestationEncoursMdl.getNbagents():prestationEncoursMdl.getPoids());
				prestationRepository.save(prestation);
			}
			
			if(Constantes.COLLECTE_ETAT_TRAITE.equals(collecteMdl.getEtat())) {
				AvpMdl avpMdl = construitAvpMdl(collecte, tournee, collecteMdl.getPrestations());
				String urlAvp = avisdePassageService.genereEtStockeAvp(avpMdl);
				collecte.setUrlavp(urlAvp);
				collecteMdlMaj.setUrlavp(urlAvp);
			}

			collecteRepository.save(collecte);
			
			collecteMdlMaj.setHdebut(collecte.getDhdebut()!=null?longToDateTimeFmtPattern(collecte.getDhdebut().getTime(),Constantes.PATTERN_HHMMSS):null);
			collecteMdlMaj.setHfin(collecte.getDhfin()!=null?longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMMSS):null);

		}catch(Exception e) {
			log.error("savecollecteencours {}",collecteMdl,e);
		}
		return collecteMdlMaj;
	}
	
	@Transactional
	private AvpMdl construitAvpMdl(final Collecte collecte, final Tournee tournee, final List<PrestationEncoursMdl> prestations) {
		AvpMdl avpMdl = new AvpMdl();
		try {
			Clientose clientose = collecte.getClientose();
			String idavp = "";
			idavp += longToDateTimeFmtPattern(tournee.getDatetournee().getTime(),Constantes.PATTERN_DATETIRET);
			idavp += ".";
			if(isDevProfile()) {
				idavp += System.currentTimeMillis();
			}else {
				idavp += tournee.getIdose();
				idavp += ".";
				idavp += clientose.getIdose();
			}
	
			avpMdl.setIdavp(idavp);
			avpMdl.setNomClient(clientose.getNom());
			avpMdl.setAdresse(clientose.getAdr1());
			avpMdl.setCodepostal(clientose.getCodepostal());
			avpMdl.setVille(clientose.getVille());
			avpMdl.setIdtourneeose(tournee.getIdose());
			avpMdl.setIdclientose(clientose.getIdose());
			
			avpMdl.setAgents(tournee.getLibequipe());
			
			if(collecte.getDhfin()!=null) {
				avpMdl.setDateAvp(longToDateTimeFmtPattern(collecte.getDhfin().getTime(),"dd/MM/yyyy"));
				avpMdl.setHeureAvp(longToDateTimeFmtPattern(collecte.getDhfin().getTime(),Constantes.PATTERN_HHMM));
			}else {
				avpMdl.setDateAvp("");
				avpMdl.setHeureAvp("");
			}
	
			avpMdl.setVehicule(tournee.getVehicule());
			
			Media mediaSignature = mediaRepository.findFirstByCollecteAndTypemedia(collecte, Media.Typemedia.SIGNATURE.toString());
			
			if(mediaSignature!=null && mediaSignature.getUrl()!=null) {
				avpMdl.setSignature(mediaSignature.getUrl());
				avpMdl.setSignaturePresente(true);
			}else {
				avpMdl.setSignaturePresente(false);
			}
			
			String infosSignataire = "";

			if(!Strings.isBlank(collecte.getReceptitre())) {
				infosSignataire += collecte.getReceptitre();
				if(!Strings.isBlank(collecte.getRecepfonction())) {
					infosSignataire += " - ";
					infosSignataire += collecte.getRecepfonction();
				}
			}
			
			avpMdl.setInfosSignataire(infosSignataire);
			
			for(PrestationEncoursMdl prestation : prestations) {
				AvpDetailMdl avpDetailMdl = new AvpDetailMdl();
				avpDetailMdl.setOperation(prestation.getOperation());
				avpDetailMdl.setDispositif(prestation.getDispositif());
				avpDetailMdl.setQteprev(String.valueOf(prestation.getQteprevue()));
				avpDetailMdl.setQtereal(String.valueOf(prestation.getQtereel()));
				avpMdl.getDetails().add(avpDetailMdl);
			}
			
			List<Media> avpimages = mediaRepository.findByCollecteAndTypemedia(collecte, Media.Typemedia.AVPIMAGE.toString());
			
			avpMdl.setPhotosPresentes(!avpimages.isEmpty());
			for(Media media : avpimages) {
				if(!Strings.isBlank(media.getUrl())) {
					AvpPhotoMdl avpPhotoMdl = new AvpPhotoMdl();
					avpPhotoMdl.setUrl(media.getUrl());
					avpPhotoMdl.setDhfmt(longToDateTimeFmtPattern(media.getDatecreation().getTime(), Constantes.PATTERN_DATEHEURE));
					avpMdl.getPhotos().add(avpPhotoMdl);
				}
			}
		}catch(Exception e) {
			log.error("construitAvpMdl : collecte : {}",collecte,e);
		}
		return avpMdl;
	}

	@Transactional
	public TourneeMdl savetourneeencours(final TourneeMdl tourneeMdl) {
		TourneeMdl tourneeMdlMaj = tourneeMdl;
		try {		
			Tournee tournee = tourneeRepository.findById(tourneeMdl.getIdtournee()).get();
			tournee.setDhfin(calculDateDefaut(tournee.getDatetournee()));
			tournee.setEtat(Constantes.TOURNEE_ETAT_FIN);
			tournee.setCleflux(deroulementService.getCleServiceOse(tournee.getToken()));
			tourneeRepository.save(tournee);

			// majOseService.majFinTournee(toTourneeInfoMdl(tourneeMdlMaj,tournee),tournee.getToken());
			majOseService.majTourneeCollecteOse(tournee);
		}catch(Exception e) {
			log.error("savetourneeencours : tourneeMdl : {}",tourneeMdl,e);
		}
		return tourneeMdlMaj;
	}
	
	@Transactional
	private TourneeInfoMdl toTourneeInfoMdl(final TourneeMdl tourneeMdl, final Tournee tournee) {
		try {
			TourneeInfoMdl tourneeInfoMdl = new TourneeInfoMdl();
			tourneeInfoMdl.setTour(String.valueOf(tournee.getNotour()));
			tourneeInfoMdl.setIdentifiant(tournee.getIdose());
			tourneeInfoMdl.setFin(tournee.getDhfin().getTime());
			tourneeInfoMdl.setCodeservice(tournee.getCodeservice());
			
			for(CollecteMdl collecteMdl : tourneeMdl.getCollectes()) {
				
				Collecte collecte = collecteRepository.findById(collecteMdl.getIdcollecte()).get();
				
				ClientMdl clientMdl = new ClientMdl();
				clientMdl.setId(collecteMdl.getIdclientose());
				clientMdl.setUrlavp(collecteMdl.getUrlavp());
				clientMdl.setDebut(collecte.getDhdebut().getTime());
				clientMdl.setFin(collecte.getDhfin().getTime());
				ContactMdl recept = new ContactMdl();
				recept.setFonction(collecte.getRecepfonction());
				recept.setTitre(collecte.getReceptitre());
				clientMdl.setRecept(recept);
				
				List<Media> mediasSigltaudio = mediaRepository.findByCollecteAndTypemedia(collecte, Media.Typemedia.SIGNALEMENTAUDIO.toString());
				if(!mediasSigltaudio.isEmpty()) {
					for(Media media : mediasSigltaudio) {
						MediaMdl mediaMdl = new MediaMdl();
						mediaMdl.setUrl(media.getUrl());
						clientMdl.getSigltaudio().add(mediaMdl);
					}
				}
				
				List<Media> mediasSigltimage = mediaRepository.findByCollecteAndTypemedia(collecte, Media.Typemedia.SIGNALEMENTIMAGE.toString());
				if(!mediasSigltimage.isEmpty()) {
					for(Media media : mediasSigltimage) {
						MediaMdl mediaMdl = new MediaMdl();
						mediaMdl.setUrl(media.getUrl());
						clientMdl.getSigltimage().add(mediaMdl);
					}
				}
				
				Media mediaSignature = mediaRepository.findFirstByCollecteAndTypemedia(collecte, Media.Typemedia.SIGNATURE.toString());
				if(mediaSignature!=null && mediaSignature.getUrl()!=null) {
					MediaMdl mediaMdl = new MediaMdl();
					mediaMdl.setUrl(mediaSignature.getUrl());
					mediaMdl.setDateheure(mediaSignature.getDatecreation().getTime());
					clientMdl.setSignature(mediaMdl);
				}
				
				Media mediaAudio = mediaRepository.findFirstByCollecteAndTypemedia(collecte, Media.Typemedia.INFOCLIENT.toString());
				if(mediaAudio!=null && mediaAudio.getUrl()!=null) {
					MediaMdl mediaMdl = new MediaMdl();
					mediaMdl.setUrl(mediaAudio.getUrl());
					mediaMdl.setDateheure(mediaAudio.getDatecreation().getTime());
					clientMdl.setAudio(mediaMdl);
				}
				
				for(PrestationEncoursMdl prestationEncoursMdl : collecteMdl.getPrestations()) {
					Prestation prestation = prestationRepository.findById(prestationEncoursMdl.getIdprestation()).get();
					OperationMdl operationMdl = new OperationMdl();
					operationMdl.setPdsreel(prestationEncoursMdl.getPoids());
					operationMdl.setId(prestation.getOpid());
					operationMdl.setDispoid(prestation.getDispoid());
					operationMdl.setQtereel(String.valueOf(prestationEncoursMdl.getQtereel()));
					
					clientMdl.getOperations().add(operationMdl);
				}
				
				tourneeInfoMdl.getClients().add(clientMdl);
			}
			
			return tourneeInfoMdl;
		}catch(Exception e) {
			log.error("savetourneeencours : tourneeMdl : {}",tourneeMdl,e);
			return null;
		}
	}
}
