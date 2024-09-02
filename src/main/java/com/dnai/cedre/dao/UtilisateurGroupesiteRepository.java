package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.UtilisateurGroupesite;
import com.dnai.cedre.domain.UtilisateurGroupesitePk;

public interface UtilisateurGroupesiteRepository extends CrudRepository<UtilisateurGroupesite, UtilisateurGroupesitePk>{
	List<UtilisateurGroupesite> findByIdutilisateur(long idutilisateur);
}
