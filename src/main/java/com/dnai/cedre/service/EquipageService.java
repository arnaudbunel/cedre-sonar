package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.model.cockpit.EquipageMdl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EquipageService extends ParentService {

	@Autowired
	private TourneeRepository tourneeRepository;
	
	@Transactional
	public List<EquipageMdl> equipages(){
		
		List<EquipageMdl> equipages = new ArrayList<>();
		
		List<String> libequipes = tourneeRepository.findDistinctLibequipe();
		
		for(String libequipe : libequipes) {
			EquipageMdl equipageMdl = new EquipageMdl(libequipe.toUpperCase());
			equipages.add(equipageMdl);
		}

		return equipages;
	}
}
