package com.sfm.obd.service;

import java.util.List;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.ValeurParametre;
import org.springframework.data.domain.Pageable;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.model.Parametre;
import com.sfm.obd.model.Utilisateur;

public interface ParametreService {

    Parametre save(Utilisateur userConnected,Parametre parametre);

    List<Parametre> findAll();

    void delete(Utilisateur userConnected,long id);

    Parametre findById(Long id);

    EntityPage<Parametre> findByKeyword(String keyword, Pageable pageable);

    ValeurParametre findParamValueByBoitier(long parametreId, Boitier boitier);
}
