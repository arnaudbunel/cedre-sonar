package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Tournee;

public interface TourneeRepository extends CrudRepository<Tournee, Long>{
	//List<Tournee> findByDatetourneeAfter(Date dateDebut);
	//List<Tournee> findByDatetournee(Date dateDebut);
	//List<Tournee> findByDatetourneeOrderByDhdebut(Date datetournee);
	List<Tournee> findByDatetourneeAndTypetourneeOrderByDhdebut(Date datetournee, String typetournee);
	
	@Query("SELECT DISTINCT t.libequipe FROM Tournee t order by t.libequipe")
	List<String> findDistinctLibequipe();
	
	//List<Tournee> findByLibequipeIgnoreCaseAndDatetourneeBetweenAndEtat(String libequipe, Date datedebut, Date datefin, String etat);
	List<Tournee> findByLibequipeIgnoreCaseAndDatetourneeBetweenAndEtatAndTypetournee(String libequipe, Date datedebut, Date datefin, String etat, String typetournee);

	//List<Tournee> findByDatetourneeBetweenAndEtat(Date datedebut, Date datefin, String etat);
	List<Tournee> findByDatetourneeBetweenAndEtatAndTypetournee(Date datedebut, Date datefin, String etat, String typetournee);
	
	//Tournee findFirstByDatetourneeAndNotourAndIdose(Date tournee, int notour, String idose);
	Tournee findFirstByDatetourneeAndNotourAndIdoseAndTypetournee(Date tournee, int notour, String idose, String typetournee);

	//List<Tournee> findByDatetourneeAndCodeserviceIgnoreCaseAndEtatNot(Date dateTournee, String codeservice, String etat);
	List<Tournee> findByDatetourneeAndCodeserviceIgnoreCaseAndEtatNotAndTypetournee(Date dateTournee, String codeservice, String etat, String typetournee);
	//List<Tournee> findByDatetourneeAndEtatNot(Date dateTournee, String etat);
	List<Tournee> findByDatetourneeAndEtatNotAndTypetournee(Date dateTournee, String etat, String typetournee);
	
	List<Tournee> findByDatetourneeAfterAndTypetourneeOrderByDhdebut(Date dateTournee, String typetournee);
	
	Tournee findFirstByTypetourneeAndCodeserviceIgnoreCaseAndEtatAndDatetourneeAfter(String typetournee, String codeservice, String etat, Date dateTournee);
}
