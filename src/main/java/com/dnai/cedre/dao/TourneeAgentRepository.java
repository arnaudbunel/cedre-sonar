package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.TourneeAgent;
import com.dnai.cedre.domain.TourneeAgentPk;

public interface TourneeAgentRepository extends CrudRepository<TourneeAgent, TourneeAgentPk>{
	List<TourneeAgent> findByIdagentose(long idagentose);
	List<TourneeAgent> findByIdtournee(long idtournee);
}
