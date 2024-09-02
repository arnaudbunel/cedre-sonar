package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Prestation;

public interface PrestationRepository extends CrudRepository<Prestation, Long>{
	List<Prestation> findByCollecte(Collecte collecte);
	
	Prestation findFirstByCollecteAndLibelle(Collecte collecte, String libelle);
	Prestation findFirstByCollecteAndPosition(Collecte collecte, int position);
}
