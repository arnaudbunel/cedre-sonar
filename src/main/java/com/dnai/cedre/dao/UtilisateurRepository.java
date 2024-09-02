package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long>{
	Utilisateur findFirstByEmail(String email);
	List<Utilisateur> findByMandantGreaterThanEqual(int mandant);
}
