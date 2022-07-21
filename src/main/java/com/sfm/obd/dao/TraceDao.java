package com.sfm.obd.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Trace;
import com.sfm.obd.myrepo.MyRepository;

@Repository
public interface TraceDao extends MyRepository<Trace, Long> {
	List<Trace> findAllByOrderByDateDesc();
	
	List<Trace> findAllByDateBetweenOrderByDateDesc(Date debut, Date fin);
	List<Trace> findAllByUserIdAndDateBetweenOrderByDateDesc(long id, Date debut, Date fin);
	
	List<Trace> findAllByUserEntrepriseAndDateBetweenOrderByDateDesc(Entreprise entreprise, Date debut, Date fin);
	List<Trace> findAllByUserIdAndUserEntrepriseAndDateBetweenOrderByDateDesc(long id, Entreprise entreprise, Date debut, Date fin);
}
