package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.HistoriqueCodeOse;

public interface HistoriqueCodeOseRepository extends CrudRepository<HistoriqueCodeOse, Long>{
	HistoriqueCodeOse findFirstByCodeserviceAndTourAndIdoseAndDatetournee(String codeservice, String tour, String idose, Date datetournee);
	List<HistoriqueCodeOse> findByDatetourneeBefore(Date datetournee);
}
