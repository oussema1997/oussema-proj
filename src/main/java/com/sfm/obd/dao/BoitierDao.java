package com.sfm.obd.dao;

import com.sfm.obd.model.Entreprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.model.Boitier;
import com.sfm.obd.myrepo.MyRepository;

import java.util.List;

@Repository
public interface BoitierDao extends MyRepository<Boitier, Long> {
	Page<Boitier> findByReferenceContainingOrImeiContaining(
			String reference,String imei,  Pageable pageable);
	
	@Query("SELECT b from Boitier b WHERE "
    		+ "(b.etat = :etat) "
    		+ "AND ("
			+ "b.reference LIKE CONCAT('%',:keyword,'%') "
			+ "OR b.imei LIKE CONCAT('%',:keyword,'%') "
			+ ")"
			)
	Page<Boitier> findByKeywordAndEtat(
			@Param("keyword") String keyword,
			@Param("etat") PairingBox etat,
			Pageable pageable);
	
	@Query("SELECT b from Boitier b WHERE "
    		+ "(b.entreprise.id = :entrepriseId) "
    		+ "AND ("
			+ "b.reference LIKE CONCAT('%',:keyword,'%') "
			+ "OR b.imei LIKE CONCAT('%',:keyword,'%') "
			+ ")"
			)
	Page<Boitier> findByKeywordAndEntreprise(
			@Param("keyword") String keyword,
			@Param("entrepriseId") long entrepriseId,
			Pageable pageable);
	
	@Query("SELECT b from Boitier b WHERE "
    		+ "(b.entreprise.id = :entrepriseId) "
    		+ "AND (b.etat = :etat) "
    		+ "AND ("
			+ "b.reference LIKE CONCAT('%',:keyword,'%') "
			+ "OR b.imei LIKE CONCAT('%',:keyword,'%') "
			+ ")"
			)
	Page<Boitier> findByKeywordAndEntrepriseAndEtat(
			@Param("keyword") String keyword,
			@Param("etat") PairingBox etat,
			@Param("entrepriseId") long entrepriseId,
			Pageable pageable);
	
	Boitier findByImei(String imei);
	List<Boitier> findByEntreprise(Entreprise entreprise);
	
}
