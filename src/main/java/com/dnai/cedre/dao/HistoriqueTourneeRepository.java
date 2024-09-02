package com.dnai.cedre.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.HistoriqueTournee;

public interface HistoriqueTourneeRepository extends CrudRepository<HistoriqueTournee, Long>{
	HistoriqueTournee findFirstByCodeserviceAndIdoseAndDhcreationAfterOrderByDhcreationDesc(String codeservice, String idose, LocalDateTime dhcreation);
	List<HistoriqueTournee> findByDhcreationBefore(LocalDateTime dhcreation);
}
