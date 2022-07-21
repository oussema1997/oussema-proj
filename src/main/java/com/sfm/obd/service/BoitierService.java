package com.sfm.obd.service;

import java.util.List;

import com.sfm.obd.model.*;
import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.BoitierEntreprise;
import com.sfm.obd.dto.BoitierParametreValeur;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.enumer.PairingBox;

public interface BoitierService {
	
	Boitier newBoitier(Boitier boitier, String versionHardware, String proj);
	
	Boitier saveBoitier(Boitier boitier);
	
    List<Boitier> findAll();
    
    void delete(Utilisateur userConnected,long id);
    
    Boitier findById(Long id);
    
    Boitier findByImei(String imei);
    
    EntityPage<Boitier> findByKeyword(Utilisateur userConnected, String keyword, Pageable pageable);

	EntityPage<Boitier> findByKeywordAndEtat(Utilisateur userConnected, String keyword, PairingBox etat,
			Pageable pageable);

	List<ValeurParametre> findBoitierParametre(Utilisateur userConnected,Boitier boitier);

	List<ValeurParametre> findBoitierParametre(Boitier boitier);

	List<ValeurParametre> updateBoitierParametre(Utilisateur userConnected,
			BoitierParametreValeur boitierParametreValeur);

	EntityPage<Boitier> affecteToEntreprise(Utilisateur userConnected, BoitierEntreprise boitierEntreprise);

	List<Boitier> findAllByEntreprise(Entreprise entreprise);
}
