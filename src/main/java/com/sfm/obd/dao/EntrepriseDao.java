package com.sfm.obd.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sfm.obd.model.Entreprise;
import com.sfm.obd.myrepo.MyRepository;

@Repository
public interface EntrepriseDao extends MyRepository<Entreprise, Long> {
	Page<Entreprise> findByNomContaining(String keyword, Pageable pageable);
	//Page<Entreprise> findByTypeEntrepriseAndNomContaining(String keyword, Pageable pageable);
}
