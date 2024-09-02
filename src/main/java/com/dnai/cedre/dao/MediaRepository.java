package com.dnai.cedre.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Media;

public interface MediaRepository extends CrudRepository<Media, Long>{
	Media findFirstByUrl(String url);
	Media findFirstByIdtourneeAndIdclientAndTypemediaAndDatecreation(String idtournee, String idclient, String typemedia, Date datecreation);
	Media findFirstByCollecteAndTypemedia(Collecte collecte, String typemedia);
	List<Media> findByCollecteAndTypemedia(Collecte collecte, String typemedia);
}
