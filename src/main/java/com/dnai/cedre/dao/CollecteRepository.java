package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Tournee;

public interface CollecteRepository extends CrudRepository<Collecte, Long>{
	List<Collecte> findByTournee(Tournee tournee);
	List<Collecte> findByTourneeAndMandant(Tournee tournee, int mandant);
	List<Collecte> findByTourneeAndClientose(Tournee tournee, Clientose clientose);
	Collecte findFirstByTourneeAndClientose(Tournee tournee, Clientose clientose);
	List<Collecte> findByTourneeOrderByDhdebut(Tournee tournee);
	
	List<Collecte> findByClientoseAndTourneeDatetournee(Clientose clientose, Date datetournee);
	List<Collecte> findByClientoseAndTourneeDatetourneeAfter(Clientose clientose, Date datetournee);
	List<Collecte> findByClientoseAndTourneeDatetourneeBetween(Clientose clientose, Date datedebut, Date datefin);
	List<Collecte> findByClientoseAndEtatAndTourneeDatetourneeBetween(Clientose clientose, String etat, Date datedebut, Date datefin);
	List<Collecte> findByClientoseAndTourneeDatetourneeBetweenAndTourneeEtat(Clientose clientose, Date datedebut, Date datefin, String etat);
	List<Collecte> findByTourneeDatetourneeBetween(Date datedebut, Date datefin);
	List<Collecte> findByEtatAndTourneeDatetourneeBetween(String etat,Date datedebut, Date datefin);
}
