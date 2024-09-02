package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.MediaDib;

public interface MediaDibRepository extends CrudRepository<MediaDib, Long>{
	List<MediaDib> findByIdcollectedib(final long idcollecteDib);
}
