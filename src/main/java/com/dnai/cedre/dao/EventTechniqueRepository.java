package com.dnai.cedre.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.EventTechnique;

public interface EventTechniqueRepository extends CrudRepository<EventTechnique, Long>{
	List<EventTechnique> findByDheventBefore(LocalDateTime dhevent);
}
