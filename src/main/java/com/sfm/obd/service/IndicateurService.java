package com.sfm.obd.service;

import java.util.List;

import com.sfm.obd.enumer.TypeBoitier;
import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.model.Indicateur;
import com.sfm.obd.model.Utilisateur;

public interface IndicateurService {
	
	Indicateur save(Utilisateur userConnected, Indicateur indicateur);
	
    List<Indicateur> findAll();
    
    void delete(Utilisateur userConnected,long id);
    
    Indicateur findById(Long id);
    
    EntityPage<Indicateur> findByKeyword(String keyword, Pageable pageable);

    //List<Indicateur> findByTypeBoitier(TypeBoitier TypeBoitier);
}
