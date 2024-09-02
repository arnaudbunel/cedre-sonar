package com.dnai.cedre.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.ClientoseRepository;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.GroupesiteClientoseRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.dao.UtilisateurGroupesiteRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.GroupesiteClientose;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.domain.TourneeAgent;
import com.dnai.cedre.domain.UtilisateurGroupesite;
import com.dnai.cedre.model.AdresseMdl;
import com.dnai.cedre.model.ClientCommunMdl;
import com.dnai.cedre.model.PositionMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.model.cockpit.Donneepoids;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParentService {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PrestationRepository prestationRepository;
	
	@Autowired
	private ClientoseRepository clientoseRepository;
	
	@Autowired
	private UtilisateurGroupesiteRepository utilisateurGroupesiteRepository;
	
	@Autowired
	private GroupesiteClientoseRepository groupesiteClientoseRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private TourneeUtilService tourneeUtilService;
		
	@Autowired
	private GeolocalisationService geolocalisationService;
	
	@Autowired
	private Environment env;
		
	protected boolean isDevProfile() {
		boolean isDevProfile = false;
		
		String[] profiles = env.getActiveProfiles();
		for(String profile : profiles) {
			if("dev".equals(profile)) {
				isDevProfile = true;
				break;
			}
		}
		
		return isDevProfile;
	}
	
	protected boolean isTestProfile() {
		boolean isProfile = false;
		
		String[] profiles = env.getActiveProfiles();
		for(String profile : profiles) {
			if("test".equals(profile)) {
				isProfile = true;
				break;
			}
		}
		
		return isProfile;
	}
	
	protected Date longToDate(long dhlong) {
		if(dhlong>0) {
			return new Date(dhlong);
		}else {
			return null;
		}
	}
	
	private String fmtAdressePourGeoloc(final AdresseMdl adresseMdl) {	
		String adresseFmt = adresseMdl.getAdr1() + ",";
		adresseFmt = adresseFmt + adresseMdl.getCodepostal() 
			+ " " + adresseMdl.getVille() + ", FR";
		
		return adresseFmt;
	}
	
	@Transactional
	protected Clientose creerClientose(final ClientCommunMdl clientMdl) {
		Clientose clientose = null;
		try {
			clientose = clientoseRepository.findFirstByIdose(clientMdl.getId());

			if(clientose==null) {
				Clientose clientoseNew = new Clientose();
				clientoseNew.setIdose(clientMdl.getId());
				clientoseNew.setAdr1(StringUtils.abbreviate(clientMdl.getAdr1(),100));
				clientoseNew.setAdr2(StringUtils.abbreviate(clientMdl.getAdr2(),100));
				clientoseNew.setCodepostal(clientMdl.getCodepostal());
				clientoseNew.setNom(clientMdl.getNom());
				clientoseNew.setVille(clientMdl.getVille());
				
				completerLatLgn(clientoseNew, clientMdl);
								
				/*
				Adresseose adresseose = adresseoseService.rechercheAdresse(StringUtils.abbreviate(clientMdl.getAdr1(),100), clientMdl.getCodepostal(), clientMdl.getVille());
				if(adresseose==null) {
					log.debug("creerClientose : creation adresse pour clientMdl : {}",clientMdl);
					adresseose = adresseoseService.creerAdresse(clientMdl.getAdr1(), clientMdl.getAdr2(), clientMdl.getCodepostal(), clientMdl.getVille());
					if(adresseose!=null) {
						adresseose = adresseoseService.geolocaliseAdresse(adresseose);
					}
				}

				if(adresseose!=null &&  !"ECHEC".equals(adresseose.getEtat())) {
					clientoseNew.setLatitude(adresseose.getLatitude());
					clientoseNew.setLongitude(adresseose.getLongitude());
					clientMdl.setLatitude(adresseose.getLatitude());
					clientMdl.setLongitude(adresseose.getLongitude());
					clientMdl.setGeoloc(true);
				}else {
					log.error("creerTournee : echec geoloc pour clientMdl {}",clientMdl);
					clientMdl.setGeoloc(false);
				}*/
				clientose = clientoseRepository.save(clientoseNew);
			}else {
				// vérification même nom
				if(!tourneeUtilService.memeNom(clientMdl,clientose)) {
					clientose.setNom(clientMdl.getNom());
					clientose = clientoseRepository.save(clientose);
				}
				
				// vérification même adresse
				if(!tourneeUtilService.memeAdresse(clientMdl,clientose)) {
					
					clientose.setAdr1(StringUtils.abbreviate(clientMdl.getAdr1(),100));
					clientose.setAdr2(StringUtils.abbreviate(clientMdl.getAdr2(),100));
					clientose.setCodepostal(clientMdl.getCodepostal());
					clientose.setVille(clientMdl.getVille());
					
					completerLatLgn(clientose, clientMdl);
										
					/*
					Adresseose adresseose = adresseoseService.rechercheAdresse(clientMdl.getAdr1(), clientMdl.getCodepostal(), clientMdl.getVille());
					if(adresseose==null) {
						log.debug("creerTournee : creation adresse pour clientMdl : " + clientMdl);
						adresseose = adresseoseService.creerAdresse(clientMdl.getAdr1(), clientMdl.getAdr2(), clientMdl.getCodepostal(), clientMdl.getVille());
						if(adresseose!=null) {
							adresseose = adresseoseService.geolocaliseAdresse(adresseose);
						}
					}
					if(adresseose!=null && !"ECHEC".equals(adresseose.getEtat())) {
						clientose.setLatitude(adresseose.getLatitude());
						clientose.setLongitude(adresseose.getLongitude());
						clientMdl.setLatitude(adresseose.getLatitude());
						clientMdl.setLongitude(adresseose.getLongitude());
						clientMdl.setGeoloc(true);
					}else {
						log.error("creerTournee : echec geoloc pour clientMdl : " + clientMdl);
						clientMdl.setGeoloc(false);
					}*/
					clientose = clientoseRepository.save(clientose);
					
				}else {
					clientMdl.setLatitude(clientose.getLatitude());
					clientMdl.setLongitude(clientose.getLongitude());
					clientMdl.setGeoloc(true);
				}
			}
		}catch(Exception e) {
			log.error("creerClientose : {}",clientMdl,e);
		}
		return clientose;
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private void completerLatLgn(Clientose clientose, ClientCommunMdl clientMdl) {
		try {
			if(clientMdl.getLatitude()>0 && clientMdl.getLongitude()>0) {
				clientose.setLatitude(round(clientMdl.getLatitude(),6));
				clientose.setLongitude(round(clientMdl.getLongitude(),6));
				clientose.setSrcgeoloc(Clientose.Srcgeoloc.OSE.toString());
				clientMdl.setGeoloc(true);
			}else {
				AdresseMdl adresseMdl = new AdresseMdl();
				adresseMdl.setAdr1(clientMdl.getAdr1());
				adresseMdl.setAdr2(clientMdl.getAdr2());
				adresseMdl.setCodepostal(clientMdl.getCodepostal());
				adresseMdl.setVille(clientMdl.getVille());
				
				PositionMdl positionMdl = geolocalisationService.geocodeAdresseStrict(fmtAdressePourGeoloc(adresseMdl));
				if(positionMdl.isTrouve()) {
					clientose.setLatitude(round(positionMdl.getLatitude(),6));
					clientose.setLongitude(round(positionMdl.getLongitude(),6));
					clientose.setSrcgeoloc(Clientose.Srcgeoloc.ADNAI.toString());
					clientMdl.setLatitude(round(positionMdl.getLatitude(),6));
					clientMdl.setLongitude(round(positionMdl.getLongitude(),6));
					clientMdl.setGeoloc(true);
				}else {
					log.error("completerLatLgn : echec geoloc pour adresseMdl {}, client.id {}",adresseMdl,clientMdl.getId());
					clientose.setLatitude(0);
					clientose.setLongitude(0);
					clientose.setSrcgeoloc(Clientose.Srcgeoloc.ADNAI.toString());
					clientMdl.setGeoloc(false);
				}
			}
		}catch(Exception e) {
			log.error("completerLatLgn",e);
		}
	}
	
	protected Integer strToInteger(String strint) {
		Integer i = null;
		try {
			strint = traitementQteOse(strint);
			if(strint!=null && !strint.isEmpty()) {
				i = Integer.valueOf(strint);
			}
		}catch(Exception e) {
			log.warn("strToInteger : " + e.toString() + " pour strint : " + strint);
		}
		return i;
	}
	
	protected int parseInt(String strentier) {
		int entier = 0;
		try {
			if(!Strings.isBlank(strentier) && strentier.matches("\\d+")) {
				entier = Integer.parseInt(strentier);
			}
		}catch(Exception e) {
			log.warn("parseInt : " + e.toString() + " avec strentier : " + strentier);
		}
		return entier;
	}
	
	protected String traitementQteOse(String strint) {
		String result = "?";
		if(strint!=null  ) {
			if(strint.matches("\\d+")) {
				result = strint;
			}else {
				result = "?";
			}
		}
		return result;
	}
	
	protected int traitementQteOseToNum(String strint) {
		int result = 0;
		try {
			if(strint!=null  ) {
				if(strint.matches("\\d+")) {
					result = Integer.parseInt(strint);
				}else {
					result = 0;
				}
			}
		}catch(NumberFormatException e) {
			result = 0;
		}
		return result;
	}
	
	protected double parsePoids(String poidsstr) {
		double poids = 0;
		try {
			if(poidsstr!=null && !poidsstr.trim().isEmpty()) {
				poids = Double.parseDouble(poidsstr);
			}
		}catch(Exception e) {
			log.warn("parsePoids : {}",poidsstr,e);
		}
		return poids;
	}
	
	protected String traitementPdsOse(String strint) {
		String result = "0";
		if(strint!=null) {
			if(strint.matches("\\d+(\\.\\d+)?")) {
				result = strint;
			}
		}
		return result;
	}
	
	protected String toLocalDateTimeFmt(final LocalDateTime localDateTime) {
		String dateTimeFmt = "";
		try {
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    dateTimeFmt = localDateTime.format(formatter);
		}catch(Exception e) {
			log.error("toLocalDateTimeFmt : {}",localDateTime,e);
		}
		return dateTimeFmt;
	}
	
	protected String dateTimeFmt(Date date) {
		if(date!=null) {
			return longToDateTimeFmt(date.getTime());
		}else {
			return "";
		}
	}
	
	protected String longToDateTimeFmt(long millis) {
		String dateTimeFmt = "";
		try {
			Instant instant = Instant.ofEpochMilli(millis);
		    LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		    dateTimeFmt = dateTime.format(formatter);
		}catch(Exception e) {
			log.error("longToDateTimeFmt : " + e.toString() + ", pour millis : " + millis);
		}
		return dateTimeFmt;
	}
	
	protected long localDateTimeToMillis(final LocalDateTime ldt) {
		ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
		return zdt.toInstant().toEpochMilli();
	}
	
	protected String longToDateTimeFmtPattern(long millis, String pattern) {
		String dateTimeFmt = "";
		try {
			Instant instant = Instant.ofEpochMilli(millis);
		    LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		    dateTimeFmt = dateTime.format(formatter);
		}catch(Exception e) {
			log.error("longToDateTimeFmt : " + e.toString() + ", pour millis : " + millis);
		}
		return dateTimeFmt;
	}
	
	protected Date parseDate(String dhstr, String pattern) {
		Date date = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			LocalDate localDate = LocalDate.parse(dhstr, formatter);
			date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}catch(Exception e) {
			log.error("parseDate : " + e.toString() + " avec dhstr : " + dhstr);
		}
		return date;
	}
	
	protected Date calculDateDebut(String codej) {
		LocalDate localDateDebut = LocalDate.now();
		if(codej!=null) {
			switch(codej) {
				case "HIER":
					localDateDebut = localDateDebut.minusDays(1);
					break;
				case "7J":
					localDateDebut = localDateDebut.minusDays(8);
					break;
				default:
					log.error("calculDateDebut : unsupported codej {}",codej);
			}
		}else {
			log.error("calculDateDebut : codej null");
		}
		return Date.from(localDateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	protected long calculDuree(Date dhdebut, Date dhfin) {
		long nbminutes = 0;
		try {
			if(dhdebut!=null && dhfin!=null) {
			    LocalDateTime ldtdebut = dhdebut.toInstant()
			    	      .atZone(ZoneId.systemDefault())
			    	      .toLocalDateTime();
			    LocalDateTime ldtfin = dhfin.toInstant()
			    	      .atZone(ZoneId.systemDefault())
			    	      .toLocalDateTime();
			 
			    Duration duration = Duration.between(ldtdebut, ldtfin);
			    nbminutes = Math.abs(duration.toMinutes());
			}
		}catch(Exception e) {
			log.error("calculDuree : " + e.toString());
		}
		return nbminutes;
	}
	
	protected String formatDuree(long dureeMinutes) {
		String dureefmt = "-";
		try {
			if(dureeMinutes>0) {
				dureefmt = (dureeMinutes / 60) + "h" + String.format("%02d", (dureeMinutes % 60));
			}else {
				dureefmt = "< 1mn";
			}
		}catch(Exception e) {
			log.error("formatDuree : " + e.toString());
		}
		return dureefmt;
	}
		
	public void logJsonTournee(TourneeInfoMdl tourneeInfoMdl) {
		try {
			log.debug("tourneeInfoMdl : " + objectMapper.writeValueAsString(tourneeInfoMdl));
		}catch(Exception e) {
			log.error("logJsonTournee : " + e.toString());
		}
	}
	
	protected boolean isTourneeInTourneeAgent(Tournee tournee, List<TourneeAgent> tourneeAgents) {
		boolean isTourneeInTourneeAgent = false;
		
		for(TourneeAgent tourneeAgent : tourneeAgents) {
			if(tourneeAgent.getIdtournee()==tournee.getId()) {
				isTourneeInTourneeAgent = true;
				break;
			}
		}
		
		return isTourneeInTourneeAgent;
	}
	
	protected String formatDistance(Long distance) {
		String distanceFmt = "-";
		try {
			if(distance!=null && distance>0) {
				DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.FRANCE);
				formatter.applyPattern("0.#");
				float distkm = ((float)distance)/1000;
				distanceFmt = formatter.format(distkm);
			}
		}catch(Exception e) {
			log.error("formatDistance : " + e.toString());
		}
		return distanceFmt;
	}
	
	@Transactional
	protected Donneepoids calculPoidsCollecte(Collecte collecte) {
		double poidsCollecte = 0;
		double poidsCollecteParagent = 0;
		Donneepoids donneepoids = new Donneepoids();
		try {
			List<Prestation> prestations = prestationRepository.findByCollecte(collecte);
			for(Prestation prestation : prestations) {
				poidsCollecte = poidsCollecte + prestation.getPoids();
				poidsCollecteParagent = poidsCollecteParagent + prestation.getPoidsparagent();
			}
			donneepoids.setPoids(poidsCollecte);
			donneepoids.setPoidsparagent(poidsCollecteParagent);
		}catch(Exception e) {
			log.error("calculPoidsCollecte : " + e.toString());
		}
		return donneepoids;
	}
	
	protected String formatPoids(double poids) {
		String poidsfmt = "-";
		try {
			if(poids>0) {
				NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRENCH);
				DecimalFormat formatter = (DecimalFormat) nf;
				formatter.applyPattern("#.##");
				poidsfmt = formatter.format(poids);
			}
		}catch(Exception e) {
			log.error("formatPoids : " + e.toString());
		}
		return poidsfmt;
	}
	
	protected boolean filtreTournee(Tournee tournee, int idcentre) {
		boolean filtreTournee = false;
		if(isDevProfile()) {
			filtreTournee = true;
		}else {
			// les centres test sont dans la série 90
			if(tournee.getIdcentre()<10) {
				if(idcentre>0) {
					filtreTournee = tournee.getIdcentre()==0 || tournee.getIdcentre()==idcentre;
				}else {
					filtreTournee = true;
				}
			}
		}
		return filtreTournee;
	}
	
	protected boolean filtreTourneeDib(final Tournee tournee) {
		boolean filtreTournee = false;
		
		LocalDate localDateJour = LocalDate.now();
		Date dateJour = Date.from(localDateJour.atStartOfDay(ZoneId.systemDefault()).toInstant());
		
		if(tournee.getDhdebut().after(dateJour)) {
			filtreTournee = true;
		}else {
			if(Tournee.Etat.FIN.toString().equals(tournee.getEtat())) {
				filtreTournee = true;
			}
		}
		return filtreTournee;
	}
	
	protected String formatTel(final String telbrut) {
		String telfmt = null;
		try {
			if(telbrut!=null && telbrut.startsWith("0")) {
				telfmt = telbrut.trim();
				telfmt = telfmt.replaceAll(" ", "");
				telfmt = telfmt.replaceAll("\\.", "");
				if(telfmt.length()==10) {
					telfmt = "+33" + telfmt.substring(1);
				}else {
					telfmt = null;
				}
			}
		}catch(Exception e) {
			log.error("formatTel : {}",telbrut,e);
		}
		return telfmt;
	}
	
	protected String extraitNumTel(final String value) {
		String valueFiltree = null;
		if(value!=null) {
			valueFiltree = value.replaceAll("\\D+","");
		}
		return valueFiltree;
	}
	
	protected double parseDouble(final String dstr) {
		double valdouble = 0;
		try {
			if(dstr!=null) {
				valdouble = Double.parseDouble(dstr);
			}
		}catch(Exception e) {
			log.warn("parseDouble : dstr : {}",dstr,e);
		}
		return valdouble;
	}
	
	protected String contruitLibPrestation(final String libelle, final String nom) {
		String libPrestation = libelle;
		if(nom!=null) {
			libPrestation += " - " + nom;
		}
		return libPrestation;
	}
	
	@Transactional
	protected Map<Long,Clientose> clientOseParPerimetre(long idutilisateur, long idgroupesite){
		Map<Long,Clientose> clientssose = new HashMap<>();
		
		List<UtilisateurGroupesite> listUtilisateurGroupesite =  utilisateurGroupesiteRepository.findByIdutilisateur(idutilisateur);
		for(UtilisateurGroupesite utilisateurGroupesite : listUtilisateurGroupesite) {
			if(idgroupesite==0 || (idgroupesite==utilisateurGroupesite.getIdgroupesite())) {
				List<GroupesiteClientose> listGroupesiteClientose = groupesiteClientoseRepository.findByIdgroupesite(utilisateurGroupesite.getIdgroupesite());
				for(GroupesiteClientose groupesiteClientose : listGroupesiteClientose) {
					Clientose clientose = clientoseRepository.findById(groupesiteClientose.getIdclientose()).get();
					clientssose.put(clientose.getId(), clientose);
				}
			}
		}
		return clientssose;
	}
	
	@Transactional
	protected boolean contientClientPerimetre(Map<Long, Clientose> clientOsePerimetre, Tournee tournee) {
		boolean contientClientPerimetre = false;
		List<Collecte> collectes = collecteRepository.findByTournee(tournee);
		for(Collecte collecte : collectes) {
			if(clientOsePerimetre.get(collecte.getClientose().getId())!=null) {
				contientClientPerimetre = true;
				break;
			}
		}
		return contientClientPerimetre;
	}
	
	protected LocalDateTime longToLocalDateTime(long dhlong) {
		if(dhlong>0) {
			Instant instant = Instant.ofEpochMilli(dhlong);
			return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		}else {
			return null;
		}
	}
}
