package com.dnai.cedre.dao;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Vehicule;

public interface VehiculeRepository extends CrudRepository<Vehicule, Long>{
	Vehicule findFirstByImmatriculation(String immatriculation);
}
