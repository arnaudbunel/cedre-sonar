package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.MandantRepository;
import com.dnai.cedre.domain.Mandant;
import com.dnai.cedre.model.cockpit.MandantMdl;

@Service
public class MandantService extends ParentService{

	@Autowired
	private MandantRepository mandantRepository;
	
	@Transactional
	public List<MandantMdl> mandants(){
		List<MandantMdl> mandantsMdl = new ArrayList<>();
				
		List<Mandant> mandants =  mandantRepository.findAllByOrderByIdAsc();
		for(Mandant mandant : mandants) {
				MandantMdl mandantMdl = new MandantMdl(mandant.getId(),mandant.getLibelle());
				mandantsMdl.add(mandantMdl);
		}
		return mandantsMdl;
	}
}
