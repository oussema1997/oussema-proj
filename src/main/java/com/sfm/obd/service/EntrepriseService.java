package com.sfm.obd.service;

import java.util.List;

import com.sfm.obd.model.EntrepriseCliente;
import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.EntrepriseDto;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Utilisateur;

public interface EntrepriseService {
	
	Entreprise save(Utilisateur userConnected,EntrepriseDto entreprise);
	
    List<Entreprise> findAll();
    
    void delete(Utilisateur userConnected,long id);
    
    Entreprise findById(Long id);
    
    EntityPage<Entreprise> findByKeyword(String keyword, Pageable pageable);

    EntityPage<EntrepriseCliente> findByEntrepriseIdAndNomContaining(long entrepriseId,String keyword, Pageable pageable);

    EntrepriseCliente saveEntrepriseCliente(Utilisateur userConnected, EntrepriseDto entreprise);
}
