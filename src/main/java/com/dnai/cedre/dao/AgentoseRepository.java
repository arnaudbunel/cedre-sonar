package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Agentose;

public interface AgentoseRepository extends CrudRepository<Agentose, Long>{
	Agentose findFirstByIdose(String idose);
	List<Agentose> findAllByOrderByNomAsc();
}
