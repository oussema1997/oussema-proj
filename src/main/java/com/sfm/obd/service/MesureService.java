package com.sfm.obd.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.mesures.IndicateurMesures;
import com.sfm.obd.dto.mesures.LastMesure;
import com.sfm.obd.model.Mesure;

public interface MesureService {

    Mesure save(Mesure mesure);

    List<Mesure> findAll();

    void delete(long id);

    Mesure findById(Long id);

    EntityPage<Mesure> listMesure(long boitier_id, long indicateur_id, Pageable pageable);

	List<Mesure> listMesureByDate(long boitier_id, long indicateur_id, Date debut, Date fin);

	LastMesure lastMesures(long idBoitier);

	List<IndicateurMesures> allMesuresByDate(long idBoitier, Date dateDebut, Date dateFin);

}
