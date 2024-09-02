package com.dnai.cedre.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.GroupesiteClientose;
import com.dnai.cedre.domain.GroupesiteClientosePk;

public interface GroupesiteClientoseRepository extends CrudRepository<GroupesiteClientose, GroupesiteClientosePk>{
	List<GroupesiteClientose> findByIdgroupesite(long idgroupesite);
}
