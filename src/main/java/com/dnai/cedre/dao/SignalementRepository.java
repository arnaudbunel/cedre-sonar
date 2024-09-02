package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Signalement;

public interface SignalementRepository extends CrudRepository<Signalement, Long>{
	List<Signalement> findByCollecte(Collecte collecte);
	Signalement findFirstByCollecteAndDatecreation(Collecte collecte,Date datecreation);
}
