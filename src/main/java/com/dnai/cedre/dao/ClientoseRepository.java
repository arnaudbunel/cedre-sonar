package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Clientose;

public interface ClientoseRepository extends CrudRepository<Clientose, Long>{
	Clientose findFirstByIdose(String idose);
	
	List<Clientose> findAllByOrderByNomAsc();
	
	List<Clientose> findFirst200ByVerifie(boolean verifie);
}
