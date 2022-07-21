package com.sfm.obd.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sfm.obd.model.Mesure;
import com.sfm.obd.myrepo.MyRepository;

public interface MesureDao extends MyRepository<Mesure, Long> {
    Page<Mesure> findByBoitierIdAndIndicateurId(long boitier_id, long indicateur_id, Pageable pageable);
    List<Mesure> findByBoitierIdAndIndicateurIdAndDateBetweenOrderByDate(long boitier_id, long indicateur_id, Date debut, Date fin);
    Mesure findTopByBoitierIdAndIndicateurIdOrderByIdDesc(long boitier_id, long indicateur_id);
    
//    @Query("SELECT new com.sfm.cfl.dto.mesures.MesureDTOVal(" 
//    		+ "m.date as date, " 
//    		+ "m.val1 as val1, "
//			+ "m.val2 as val2, " 
//    		+ "m.val3 as val3 ) "
//    		+ "FROM Mesure m " + "WHERE "
//    				+ "m.boitier.id = :idBoitier "
//    				+ "AND m.indicateur.id = :idIndicateur "
//    				+ "AND m.date between :startDate AND :endDate")
//	public List<MesureDTOVal> findByBoitierAndIndicateur(
//			@Param("idBoitier") long idBoitier,
//			@Param("idIndicateur") long idIndicateur, 
//			@Param("startDate") Date startDate,
//			@Param("endDate") Date endDate);
    
    @Query("SELECT m.datems as date, m.val1 as valeur "
    		+ "FROM Mesure m " + "WHERE "
    				+ "m.boitier.id = :idBoitier "
    				+ "AND m.indicateur.id = :idIndicateur "
    				+ "AND m.date between :startDate AND :endDate")
	public List<?> findByBoitierAndIncateurVal1(
			@Param("idBoitier") long idBoitier,
			@Param("idIndicateur") long idIndicateur, 
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);
    
    @Query("SELECT m.datems as date, m.val2 as valeur "
    		+ "FROM Mesure m " + "WHERE "
    				+ "m.boitier.id = :idBoitier "
    				+ "AND m.indicateur.id = :idIndicateur "
    				+ "AND m.date between :startDate AND :endDate")
	public List<?> findByBoitierAndIncateurVal2(
			@Param("idBoitier") long idBoitier,
			@Param("idIndicateur") long idIndicateur, 
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);  
    
    @Query("SELECT m.datems as date, m.val3 as valeur "
    		+ "FROM Mesure m " + "WHERE "
    				+ "m.boitier.id = :idBoitier "
    				+ "AND m.indicateur.id = :idIndicateur "
    				+ "AND m.date between :startDate AND :endDate")
	public List<?> findByBoitierAndIncateurVal3(
			@Param("idBoitier") long idBoitier,
			@Param("idIndicateur") long idIndicateur, 
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT m.datems as date, m.val4 as valeur "
			+ "FROM Mesure m " + "WHERE "
			+ "m.boitier.id = :idBoitier "
			+ "AND m.indicateur.id = :idIndicateur "
			+ "AND m.date between :startDate AND :endDate")
	public List<?> findByBoitierAndIncateurVal4(
			@Param("idBoitier") long idBoitier,
			@Param("idIndicateur") long idIndicateur,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT m.datems as date, m.val5 as valeur "
			+ "FROM Mesure m " + "WHERE "
			+ "m.boitier.id = :idBoitier "
			+ "AND m.indicateur.id = :idIndicateur "
			+ "AND m.date between :startDate AND :endDate")
	public List<?> findByBoitierAndIncateurVal5(
			@Param("idBoitier") long idBoitier,
			@Param("idIndicateur") long idIndicateur,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);
    
}
