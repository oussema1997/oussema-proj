package com.sfm.obd.service;

import java.util.Date;
import java.util.List;

import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;

public interface TraceService {
	
	Trace save(Trace trace);
	
    List<Trace> findAll();
    
    void delete(long id);
    
    Trace findById(Long id);
    
    long count();
    
    List<Trace> findAllByUserIdAndDateBetween(Utilisateur userConnected, long id, Date debut, Date fin);
    
}
