package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.model.ExportEtudeMdl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExportEtudeService extends ParentService{

	@Autowired
	private PrestationRepository prestationRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Transactional
	public List<ExportEtudeMdl> exportEtude(){
		List<ExportEtudeMdl> listeExportEtudeMdl = new ArrayList<>();
		try {
			Date datedebut = parseDate("2019-06-03","yyyy-MM-dd");
			Date datefin = parseDate("2019-06-30","yyyy-MM-dd");
			
			List<Collecte> collectes =  collecteRepository.findByTourneeDatetourneeBetween(datedebut, datefin);
			
			Map<String,ExportEtudeMdl> mapExportEtudeMdl = new HashMap<>();
			
			for(Collecte collecte : collectes) {
				Clientose clientose = collecte.getClientose();
				Date dateTournee = collecte.getTournee().getDatetournee();
				String idose = clientose.getIdose();
				ExportEtudeMdl exportEtudeMdl = mapExportEtudeMdl.get(idose);
				if(exportEtudeMdl==null) {
					exportEtudeMdl = new ExportEtudeMdl();
					exportEtudeMdl.setAdresse(clientose.getAdr1() + " " + clientose.getCodepostal() + " " + clientose.getVille());
					exportEtudeMdl.setLatitude(clientose.getLatitude());
					exportEtudeMdl.setLongitude(clientose.getLongitude());
					exportEtudeMdl.setClient(clientose.getNom());
				}
				
				List<Prestation> prestations =  prestationRepository.findByCollecte(collecte);
				int qte = 0;
				for(Prestation prestation : prestations) {
					qte = qte + parseInt(prestation.getQtereel());
				}
				
				int numsem = calculNumSem(dateTournee,datedebut);
				if(numsem==1) {
					exportEtudeMdl.setVsem1(exportEtudeMdl.getVsem1() + qte);
				}else if(numsem==2) {
					exportEtudeMdl.setVsem2(exportEtudeMdl.getVsem2() + qte);
				}else if(numsem==3) {
					exportEtudeMdl.setVsem3(exportEtudeMdl.getVsem3() + qte);
				}else if(numsem==4) {
					exportEtudeMdl.setVsem4(exportEtudeMdl.getVsem4() + qte);
				}else {
					log.error("numsem non supporte : " + numsem);
				}
				mapExportEtudeMdl.put(idose, exportEtudeMdl);
			}
			listeExportEtudeMdl = new ArrayList<>(mapExportEtudeMdl.values());
			log.info("listeExportEtudeMdl : " + listeExportEtudeMdl.size());
		}catch(Exception e) {
			log.error(e.toString());
		}
		return listeExportEtudeMdl;
	}
	
	private int calculNumSem(Date datetournee, Date datedebut) {
		int numsem = 0;
		
	    long diffInMillies = Math.abs(datetournee.getTime() - datedebut.getTime());
	    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		
	    if(diff<7) {
	    	numsem = 1;
	    }else if(diff>=7 && diff <14) {
	    	numsem = 2;
	    }else if(diff>=14 && diff <21) {
	    	numsem = 3;
	    }else if(diff>=21 && diff <29) {
	    	numsem = 4;
	    }
	    
		return numsem;
	}
	
}
