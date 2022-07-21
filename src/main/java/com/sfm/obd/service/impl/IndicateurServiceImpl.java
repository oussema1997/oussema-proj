package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sfm.obd.enumer.TypeBoitier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.IndicateurDao;
import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.model.Indicateur;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.IndicateurService;


@Service(value = "indicateurService")
public class IndicateurServiceImpl implements IndicateurService {
	
	@Autowired
	private IndicateurDao indicateurDao;
	
	@Autowired
	private TraceDao traceDao;

	@Override
	public Indicateur save(Utilisateur userConnected,Indicateur indicateur) {
		
		// Préparation de l'action pour la traçabilité
		String action;
		if(indicateur.getId() == 0) {
    		action = "Ajout indicateur : " + System.lineSeparator();
    	} else {
    		action = "Modification indicateur : " +  System.lineSeparator();
    		Indicateur oldIndicateur = indicateurDao.findById(indicateur.getId()).get();
    		// Traces de l'ancien
			action += oldIndicateur.toString() + " ==> " + System.lineSeparator();
    	}		
		
		indicateur = indicateurDao.save(indicateur);
		indicateurDao.refresh(indicateur);
		
		// Traces du nouveau
		action += indicateur.toString();
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
		
		return indicateur;
	}

	@Override
	public List<Indicateur> findAll() {
		List<Indicateur> list = new ArrayList<>();
		indicateurDao.findAll().iterator().forEachRemaining(list::add);

		return list;
	}

	@Override
	public void delete(Utilisateur userConnected,long id) {
		
		Indicateur indicateur = findById(id);
		
		// Préparation de l'action pour la traçabilité
		String action = "Suppression indicateur : " + System.lineSeparator() + indicateur.toString();
		
		indicateurDao.deleteById(id);
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
		
	}

	@Override
	public Indicateur findById(Long id) {
		return indicateurDao.findById(id).get();
	}
	
	@Override
	public EntityPage<Indicateur> findByKeyword(String keyword, Pageable pageable) {
		
		Page<Indicateur> categoriesPage = indicateurDao.findByLibelleContaining(keyword,pageable);

		EntityPage<Indicateur> indicateurs = new EntityPage<Indicateur>();

		indicateurs.setList(categoriesPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(categoriesPage.getNumberOfElements());
		pageUtil.setNombrePage(categoriesPage.getTotalPages());
		pageUtil.setNumeroPage(categoriesPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(categoriesPage.getTotalElements());

		indicateurs.setPageUtil(pageUtil);

		return indicateurs;
	}

	//@Override
	//public List<Indicateur> findByTypeBoitier(TypeBoitier TypeBoitier) {
	//	return indicateurDao.findByTypeBoitier(TypeBoitier);
	//}


}
