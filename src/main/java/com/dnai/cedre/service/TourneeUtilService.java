package com.dnai.cedre.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.model.ClientCommunMdl;
import com.dnai.cedre.model.ose.AgtMdl;
import com.dnai.cedre.model.ose.DispoMdl;
import com.dnai.cedre.model.ose.GetSceResponseMdl;
import com.dnai.cedre.model.ose.OpMdl;
import com.dnai.cedre.model.ose.OperationsMdl;
import com.dnai.cedre.model.ose.SceTourneeMdl;
import com.dnai.cedre.util.Constantes;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TourneeUtilService /*extends ParentService*/{
	@Autowired
	private ProfileUtilService profileUtilService;

	String formatConsigne(DispoMdl dispo) {
		String consigne = null;
		if(!Strings.isNullOrEmpty(dispo.getValue())) {
			consigne = dispo.getValue() + " . ";
		}
		if(!Strings.isNullOrEmpty(dispo.getColcons())) {
			consigne = (consigne!=null?consigne:"") + dispo.getColcons();
		}
		return consigne;
	}
	
	String genereCleOseOperation(final OpMdl opMdl, final DispoMdl dispoMdl) {
		String cleOseOperation = "";
		if(opMdl!=null) {
			cleOseOperation = StringUtils.defaultIfEmpty(opMdl.getId(), "");
			cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(opMdl.getCompo(), "");
			cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(opMdl.getNom(), "");
			cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(opMdl.getType(), "");
			cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(opMdl.getPrestation(), "");
			if(dispoMdl!=null) {
				cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(dispoMdl.getColetage(), "");
				cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(dispoMdl.getColcons(), "");
				cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(dispoMdl.getId(), "");
				cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(dispoMdl.getNom(), "");
				cleOseOperation = cleOseOperation + StringUtils.defaultIfEmpty(dispoMdl.getDechet(), "");
			}
		}
		
		return DigestUtils.md5Hex(cleOseOperation).toUpperCase();
	}
	
	boolean calculInfocovid(final OperationsMdl operationsMdl) {
		boolean infocovid = false;
		try {
			for(OpMdl op : operationsMdl.getOp()) {
				if(op.getDispo()!=null && !op.getDispo().isEmpty()) {
					for(DispoMdl dispo : op.getDispo()) {
						String nomDispo = dispo!=null?dispo.getNom():null;
						if(!Strings.isNullOrEmpty(nomDispo)) {
							String nomDispoLow = nomDispo.toLowerCase();
							if(nomDispoLow.contains(Constantes.DISPO_ALU.toLowerCase()) 
									|| nomDispoLow.contains(Constantes.DISPO_SAC.toLowerCase())
									|| nomDispoLow.contains(Constantes.DISPO_CORBEILLE.toLowerCase())) {
								infocovid = true;
								break;
							}
						}
					}
				}
			}
		}catch(Exception e) {
			log.error("calculInfocovid : {}",operationsMdl,e);
		}
		return infocovid;
	}
	
	String calculCodetype(OpMdl op) {
		String codetype = "?";
		try {
			if(op!=null && op.getPrestation()!=null && !op.getPrestation().isEmpty()) {
				String prestation = op.getPrestation();
				switch(prestation) {
					case Constantes.PRESTATION_COLLECTE:
					case Constantes.PRESTATION_COLLECTE_MIN:
					case Constantes.PRESTATION_COLLECTE_LEMON:
						codetype = "C";
						break;
					case Constantes.PRESTATION_DEPOT:
						codetype = "D";
						break;
					case Constantes.PRESTATION_DEPOTRETRAIT:
						codetype = "D/R";
						break;
					case Constantes.PRESTATION_FOURNITURE:
						codetype = "F";
						break;
					case Constantes.PRESTATION_RETRAIT:
						codetype = "R";
						break;
					case Constantes.PRESTATION_RETRAIT_DEFINITIF:
						codetype = "RD";
						break;
					case Constantes.PRESTATION_MAIN_DOEUVRE:
						codetype = "MO";
						break;
					case Constantes.PRESTATION_VIDAGE:
						codetype = "V";
						break;
					case Constantes.PRESTATION_EMPORT:
						codetype = "E";
						break;
					case Constantes.PRESTATION_TRANSPORT:
						codetype = "T";
						break;
					default:
						log.warn("calculCodetype : unsupported prestation {}",prestation);
				}
			}
		}catch(Exception e) {
			log.error("calculCodetype : " + e.toString() + ", pour op : " + op);
		}
		
		return codetype;
	}
	
	SceTourneeMdl extraitTourCourant(GetSceResponseMdl getSceResponseMdl, String identifiant, String tour) {
		SceTourneeMdl sceTourneeMdl = null;
		try {
			for(SceTourneeMdl sceTourneeTempMdl : getSceResponseMdl.getSce()) {
				if(sceTourneeTempMdl.getId().equals(identifiant) && sceTourneeTempMdl.getTour().equals(tour)) {
					sceTourneeMdl = sceTourneeTempMdl;
					break;
				}
			}
		}catch(Exception e) {
			log.error("extraitTourCourant : " + e.toString());
		}
		return sceTourneeMdl;
	}
	
	int extraitCentreId(SceTourneeMdl sceTourneeMdl) {
		int centreId = 99;
		try {
			String centreIdBrut = sceTourneeMdl.getCentreID();
			if(centreIdBrut!=null && !centreIdBrut.isEmpty()) {
				centreId = Integer.parseInt(centreIdBrut);
			}
		}catch(Exception e) {
			log.error("extraitCentreId : " + e.toString());
		}
		return centreId;
	}
	
	String extraitLibequipe(SceTourneeMdl sceTourneeMdl) {
		String libequipe = null;
		try {
			List<String> listNom = new ArrayList<>();
			for(AgtMdl agtMdl : sceTourneeMdl.getRessources().getAgts().getAgt()) {
				listNom.add(agtMdl.getNom());
			}
			libequipe = String.join(" - ", listNom);
		}catch(Exception e) {
			log.error("extraitLibequipe : " + e.toString());
		}
		return libequipe;
	}
	
	Date extraitDateTournee(String datefmt) {
		Date datetournee = null;
		try {
			if(profileUtilService.isDevProfile()) {
				LocalDate localDate = LocalDate.now();
				datetournee = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			}else {
				// ex : 2018-10-15
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
				LocalDate localDate = LocalDate.parse(datefmt, formatter);
				datetournee = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			}
		}catch(Exception e) {
			log.error("extraitDateTournee : " + e.toString());
		}
		return datetournee;
	}
	
	boolean memeAdresse(ClientCommunMdl clientMdl, Clientose clientose) {		
		return memeElementAdresse(clientMdl.getAdr1(),clientose.getAdr1()) && 
				memeElementAdresse(clientMdl.getAdr2(),clientose.getAdr2()) && 
				memeElementAdresse(clientMdl.getCodepostal(),clientose.getCodepostal()) &&
				memeElementAdresse(clientMdl.getVille(),clientose.getVille());
	}
	
	boolean memeNom(ClientCommunMdl clientMdl, Clientose clientose) {
		return memeElementAdresse(clientMdl.getNom(),clientose.getNom());
	}
	
	boolean memeElementAdresse(String elementClientMdl, String elementClientose) {
		boolean memeElementAdresse = true;
		
		if(elementClientMdl!=null && elementClientose!=null) {
			memeElementAdresse = (elementClientMdl.equals(elementClientose));
		}else if(elementClientMdl==null && elementClientose==null) {
			memeElementAdresse = true;
		}else {
			memeElementAdresse = false;
		}
		return memeElementAdresse;
	}
}
