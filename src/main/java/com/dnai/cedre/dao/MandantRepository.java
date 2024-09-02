package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Mandant;

public interface MandantRepository extends CrudRepository<Mandant, Long>{
	List<Mandant> findAllByOrderByIdAsc();
}
