package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.sfm.obd.enumer.TypeBoitier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.BoitierDao;
import com.sfm.obd.dao.ParametreDao;
import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dao.ValeurParametreDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Parametre;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.model.ValeurParametre;
import com.sfm.obd.service.ParametreService;


@Service(value = "parametreService")
public class ParametreServiceImpl implements ParametreService {

	@Autowired
	private ParametreDao parametreDao;

	@Autowired
	private TraceDao traceDao;

	@Autowired
	private BoitierDao boitierDao;

	@Autowired
	private ValeurParametreDao valeurParametreDao;

	@Override
	public Parametre save(Utilisateur userConnected,Parametre parametre) {

		boolean addToAllBoitier = false;

		// Préparation de l'action pour la traçabilité
		String action;
		if(parametre.getId() == 0) {
			action = "Ajout paramètre : " + System.lineSeparator();
			addToAllBoitier = true;
		} else {
			action = "Modification paramètre : " +  System.lineSeparator();
			Parametre oldParametre = parametreDao.findById(parametre.getId()).get();
			// Traces de l'ancien
			action += oldParametre.toString() + " ==> " + System.lineSeparator();
		}

		parametre = parametreDao.save(parametre);
		parametreDao.refresh(parametre);

		// Traces du nouveau
		action += parametre.toString();

		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));

		// Add To All Boitier
		if (addToAllBoitier) {
			List<Boitier> boitiers = boitierDao.findAll();
			if (!boitiers.isEmpty())
				for (Boitier boitier : boitiers) {
					try {
						valeurParametreDao.save(new ValeurParametre(parametre.getValeurParDefaut(), boitier, parametre));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		}

		return parametre;
	}

	@Override
	public List<Parametre> findAll() {
		List<Parametre> list = new ArrayList<>();
		parametreDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public void delete(Utilisateur userConnected,long id) {

		Parametre parametre = findById(id);

		// Préparation de l'action pour la traçabilité
		String action = "Suppression paramètre : " + System.lineSeparator() + parametre.toString();

		parametreDao.deleteById(id);

		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
	}

	@Override
	public Parametre findById(Long id) {
		return parametreDao.findById(id).get();
	}

	@Override
	public EntityPage<Parametre> findByKeyword(String keyword, Pageable pageable) {

		Page<Parametre> categoriesPage = parametreDao.findByAttributContaining(keyword,pageable);

		EntityPage<Parametre> parametres = new EntityPage<Parametre>();

		parametres.setList(categoriesPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(categoriesPage.getNumberOfElements());
		pageUtil.setNombrePage(categoriesPage.getTotalPages());
		pageUtil.setNumeroPage(categoriesPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(categoriesPage.getTotalElements());

		parametres.setPageUtil(pageUtil);

		return parametres;
	}

	@Override
	public ValeurParametre findParamValueByBoitier(long parametreId, Boitier boitier){
		Parametre parametre = findById(parametreId);
		return valeurParametreDao.findByParametreAndBoitier(parametre, boitier);
	}


}
