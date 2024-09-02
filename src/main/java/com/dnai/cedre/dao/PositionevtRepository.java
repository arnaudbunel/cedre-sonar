package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Positionevt;
import com.dnai.cedre.domain.Tournee;

public interface PositionevtRepository extends CrudRepository<Positionevt, Long>{
	Positionevt findFirstByTourneeAndEvt(Tournee tournee, String evt);
	List<Positionevt> findByDhevtBefore(Date dhevent);
}
