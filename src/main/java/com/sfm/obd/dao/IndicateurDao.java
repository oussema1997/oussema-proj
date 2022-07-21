package com.sfm.obd.dao;

import com.sfm.obd.enumer.TypeBoitier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sfm.obd.model.Indicateur;
import com.sfm.obd.myrepo.MyRepository;

import java.util.List;

@Repository
public interface IndicateurDao extends MyRepository<Indicateur, Long> {
	Page<Indicateur> findByLibelleContaining(String keyword, Pageable pageable);
	//List<Indicateur> findByTypeBoitier(TypeBoitier TypeBoitier);

}
