package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.TranscoSignalement;

public interface TranscoSignalementRepository extends CrudRepository<TranscoSignalement, Long>{
	TranscoSignalement findFirstByMotifadnaiAndIdmandant(String motif, int idmandant);
	List<TranscoSignalement> findByIdmandantAndActif(int mandant, boolean actif);
	TranscoSignalement findFirstByIdcedreAndIdmandant(String idcedre, int idmandant);
}
