package com.sfm.obd.dao;

import com.sfm.obd.model.*;
import com.sfm.obd.myrepo.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuiviVoitureDao extends MyRepository<SuiviVoiture, Long> {

    SuiviVoiture findByUserIdAndVoitureId(long user_id, long wagon_id);
    Page<SuiviVoiture> findByUserIdAndVoitureMatriculeContaining(long user_id, String recherche, Pageable pageable);
    List<SuiviVoiture> findByVoiture(Voiture voiture);
    List<SuiviVoiture> findByUser(Utilisateur user);

    @Query("SELECT s from SuiviVoiture s WHERE "
            + "(s.voiture.id = :voitureId) "
            + "AND ( "
            + "s.user.lastname LIKE CONCAT('%',:keyword,'%') "
            + "OR s.user.firstname LIKE CONCAT('%',:keyword,'%') "
            + "OR s.user.phone LIKE CONCAT('%',:keyword,'%') "
            + "OR s.user.email LIKE CONCAT('%',:keyword,'%') "
            + ")")
    List<SuiviVoiture> findByVoitureAndKeywordOnUser(
            @Param("voitureId") long voitureId,
            @Param("keyword") String keyword
    );
}
