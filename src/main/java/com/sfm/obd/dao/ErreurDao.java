package com.sfm.obd.dao;

import com.sfm.obd.model.Erreur;
import com.sfm.obd.myrepo.MyRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErreurDao extends MyRepository<Erreur, Long> {
}
