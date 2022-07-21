package com.sfm.obd.dao;

import com.sfm.obd.enumer.TypeAlarme;
import com.sfm.obd.model.TypeEtat;
import com.sfm.obd.myrepo.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TypeEtatDao extends MyRepository<TypeEtat, Long> {
    Page<TypeEtat> findByLibelleContainingAndDescriptionContaining(String libelle, String description, Pageable pageable);
    TypeEtat findByTypeAlarme(TypeAlarme typeAlarme);
}

