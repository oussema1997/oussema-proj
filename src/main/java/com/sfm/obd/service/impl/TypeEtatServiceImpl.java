package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sfm.obd.enumer.TypeAlarme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dao.TypeEtatDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.TypeEtat;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.TypeEtatService;

@Service(value = "typeEtatService")
public class TypeEtatServiceImpl implements TypeEtatService {

    @Autowired
    private TypeEtatDao typeEtatDao;

    @Autowired
    private TraceDao traceDao;

    @Override
    public TypeEtat save(Utilisateur userConnected, TypeEtat typeEtat) {
        // Préparation de l'action pour la traçabilité
        String action;
        if(typeEtat.getId() == 0) {
            action = "Ajout typeEtat : " + System.lineSeparator();
        } else {
            action = "Modification typeEtat : " +  System.lineSeparator();
            TypeEtat oldTypeEtat = typeEtatDao.findById(typeEtat.getId()).get();
            // Traces de l'ancien
            action += oldTypeEtat.toString() + " ==> " + System.lineSeparator();
        }

        typeEtat = typeEtatDao.save(typeEtat);
        typeEtatDao.refresh(typeEtat);

        // Traces du nouveau
        action += typeEtat.toString();

        // Sauvegarder la trace
        traceDao.save(new Trace(new Date(), action, userConnected));

        return typeEtat;
    }

    @Override
    public List<TypeEtat> findAll() {
        List<TypeEtat> list = new ArrayList<>();
        typeEtatDao.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public void delete(Utilisateur userConnected,long id) {
        TypeEtat typeEtat = findById(id);

        // Préparation de l'action pour la traçabilité
        String action = "Suppression typeEtat : " + System.lineSeparator() + typeEtat.toString();

        typeEtatDao.deleteById(id);

        // Sauvegarder la trace
        traceDao.save(new Trace(new Date(), action, userConnected));
    }

    @Override
    public TypeEtat findById(Long id) {
        return typeEtatDao.findById(id).get();
    }

    @Override
    public EntityPage<TypeEtat> findByKeyword(String recherche, Pageable pageable) {
        Page<TypeEtat> typeEtats = typeEtatDao.findByLibelleContainingAndDescriptionContaining(recherche, recherche, pageable);

        EntityPage<TypeEtat> typeEtatEntityPage = new EntityPage<TypeEtat>();

        typeEtatEntityPage.setList(typeEtats.getContent());

        PageUtil pageUtil = new PageUtil();
        pageUtil.setNombreElementParPage(typeEtats.getNumberOfElements());
        pageUtil.setNombrePage(typeEtats.getTotalPages());
        pageUtil.setNumeroPage(typeEtats.getNumber() + 1);
        pageUtil.setNombreTotalElement(typeEtats.getTotalElements());

        typeEtatEntityPage.setPageUtil(pageUtil);

        return typeEtatEntityPage;
    }

    @Override
    public TypeEtat findByTypeAlarme(TypeAlarme typeAlarme) {
        return typeEtatDao.findByTypeAlarme(typeAlarme);
    }
}

