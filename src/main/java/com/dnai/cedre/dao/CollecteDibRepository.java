package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.CollecteDib;
import com.dnai.cedre.domain.Tournee;

public interface CollecteDibRepository extends CrudRepository<CollecteDib, Long>{
	CollecteDib findFirstByTourneeAndClientose(Tournee tournee, Clientose clientose);
	List<CollecteDib> findByTournee(Tournee tournee);
	
	List<CollecteDib> findByClientoseAndEtatAndTourneeDatetourneeBetween(Clientose clientose, String etat, Date datedebut, Date datefin);
	
	List<CollecteDib> findByClientoseAndTourneeDatetourneeBetween(Clientose clientose, Date datedebut, Date datefin);
	
	List<CollecteDib> findByEtatAndTourneeDatetourneeBetween(String etat,Date datedebut, Date datefin);
	
	List<CollecteDib> findByTourneeDatetourneeBetween(Date datedebut, Date datefin);
}
