package com.sfm.obd.service;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.enumer.TypeAlarme;
import com.sfm.obd.model.TypeEtat;
import com.sfm.obd.model.Utilisateur;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeEtatService {

    TypeEtat save(Utilisateur userConnected, TypeEtat typeEtat);

    List<TypeEtat> findAll();

    void delete(Utilisateur userConnected,long id);

    TypeEtat findById(Long id);

    EntityPage<TypeEtat> findByKeyword(String recherche, Pageable pageable);

    TypeEtat findByTypeAlarme(TypeAlarme typeAlarme);

}
