package com.sfm.obd.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sfm.obd.model.Parametre;
import com.sfm.obd.myrepo.MyRepository;

@Repository
public interface ParametreDao extends MyRepository<Parametre, Long> {
	Page<Parametre> findByAttributContaining(String keyword, Pageable pageable);
}
