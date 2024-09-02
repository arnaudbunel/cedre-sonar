package com.dnai.cedre.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.MessageAtlas;
import com.dnai.cedre.domain.Tournee;

public interface MessageAtlasRepository extends CrudRepository<MessageAtlas, Long> {
	List<MessageAtlas> findByTourneeAndLecture(Tournee tournee, boolean lecture);
	List<MessageAtlas> findByDhreceptionIsNull();
	List<MessageAtlas> findByDhsaisieBefore(LocalDateTime dhsaisie);
}
