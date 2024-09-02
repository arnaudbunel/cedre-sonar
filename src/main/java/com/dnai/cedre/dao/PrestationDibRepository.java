package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.CollecteDib;
import com.dnai.cedre.domain.PrestationDib;

public interface PrestationDibRepository extends CrudRepository<PrestationDib, Long>{
	PrestationDib findFirstByCollectedibAndPosition(CollecteDib collectedib, int position);
	List<PrestationDib> findByCollectedib(CollecteDib collectedib);
}
