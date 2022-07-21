package com.sfm.obd.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.model.MesureBrut;

public interface MesureBrutService {
	
	MesureBrut save(MesureBrut mesureBrut);
	
    List<MesureBrut> findAll();
    
    void delete(long id);
    
    MesureBrut findById(Long id);
    
    EntityPage<MesureBrut> findByKeyword(String keyword, Pageable pageable);

	List<MesureBrut> brutesByDate(Date dateDebut, Date dateFin);
    
    
}
