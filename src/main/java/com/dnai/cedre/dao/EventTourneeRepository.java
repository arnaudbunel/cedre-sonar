package com.dnai.cedre.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.EventTournee;

public interface EventTourneeRepository extends CrudRepository<EventTournee, Long>{
	List<EventTournee> findByDhreceptionIsNull();
	EventTournee findFirstByIdtourneeAndDhreceptionIsNotNullAndDhactionIsNull(final long idtournee);
	List<EventTournee> findByDhsaisieBefore(LocalDateTime dhsaisie);
}
