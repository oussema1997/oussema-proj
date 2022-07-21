package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sfm.obd.enumer.TypeBoitier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.BoitierDao;
import com.sfm.obd.dao.EntrepriseDao;
import com.sfm.obd.dao.ParametreDao;
import com.sfm.obd.dao.TimelineBoxDao;
import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dao.ValeurParametreDao;
import com.sfm.obd.dto.BoitierEntreprise;
import com.sfm.obd.dto.BoitierParametreValeur;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.ErrorException;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Parametre;
import com.sfm.obd.model.TimelineBox;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.model.ValeurParametre;
import com.sfm.obd.service.BoitierService;


@Service(value = "boitierService")
public class BoitierServiceImpl implements BoitierService {
	
	@Autowired
	private BoitierDao boitierDao;
	
	@Autowired
	private EntrepriseDao entrepriseDao;
	
	@Autowired
	private TraceDao traceDao;
	
	@Autowired
	private TimelineBoxDao timelineBoxDao;
	
	@Autowired
	private ParametreDao parametreDao;
	
	@Autowired
	private ValeurParametreDao valeurParametreDao;
	
	@Override
	public Boitier saveBoitier(Boitier boitier) {
		return boitierDao.save(boitier);
	}
	
	@Override
	public Boitier newBoitier(final Boitier boitier, String versionHardware, String proj) {
		if (versionHardware != "") {
			// Enregistrer le boitier sans la référence
			boitierDao.save(boitier);
			boitierDao.refresh(boitier);

			//Formatter la référence selon la version HardWare et l'id
			//String version = "";
			//version += versionHardware;
			//while (version.length() < 3) {
			//	version = "0" + version;
			//}

			String identifiant = "";
			identifiant += boitier.getId();
			while (identifiant.length() < 5) {
				identifiant = "0" + identifiant;
			}

			// Mettre à jour le boitier
			boitier.setReference(proj+"-"+identifiant); //boitier.setReference(proj+"-"+version+"-"+identifiant);

			boitierDao.save(boitier);
			boitierDao.refresh(boitier);
			
			// Créer les valeurs par défaut des paramètres pour ce boitier
			List<Parametre> parametres = parametreDao.findAll();
			for (Parametre parametre : parametres) {
				valeurParametreDao.save(new ValeurParametre(parametre.getValeurParDefaut(), boitier, parametre));
			}

			// Timeline
			timelineBoxDao.save(
					new TimelineBox(new Date(), PairingBox.Manufactured, null, boitier)
					);
		}
		
		return boitier;
	}
	
	@Override
	public List<ValeurParametre> findBoitierParametre(Utilisateur userConnected, Boitier boitier){
		
		if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
			// Il a le droit
		} else {
			// S'assurer qu'il est de la même entreprise
			boitier = boitierDao.findById(boitier.getId()).get();
			if(boitier.getEntreprise() == null) throw new NotAuthorizedException();
			if (boitier.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
				throw new NotAuthorizedException();
			}		
		}
		return valeurParametreDao.findByBoitier(boitier);
	}
	
	@Override
	public List<ValeurParametre> findBoitierParametre(Boitier boitier){
		return valeurParametreDao.findByBoitier(boitier);
	}
	
	@Override
	public List<ValeurParametre> updateBoitierParametre(Utilisateur userConnected, BoitierParametreValeur boitierParametreValeur){
		Boitier boitier = boitierDao.findById(boitierParametreValeur.getBoitier().getId()).get();
		if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
			// Il a le droit
		} else {
			// S'assurer qu'il est de la même entreprise
			
			if(boitier.getEntreprise() == null) throw new NotAuthorizedException();
			if (boitier.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
				throw new NotAuthorizedException();
			}		
		}
		
		ValeurParametre valeurParametre = valeurParametreDao.findByParametreAndBoitier(
				boitierParametreValeur.getParametre(), boitierParametreValeur.getBoitier());
		
		// Préparation de l'action pour la traçabilité
		String action = "Modification paramètre : " + System.lineSeparator()
		+ "Boitier : " + boitier.toString() + System.lineSeparator()
		+ valeurParametre.toString() + "==>";

		valeurParametre.setValeur(boitierParametreValeur.getValeur());
		valeurParametre = valeurParametreDao.save(valeurParametre);

		
		// Traces du nouveau
		action += valeurParametre.toString();
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
		
		return valeurParametreDao.findByBoitier(boitier);
		
	}
	
	@Override
	public EntityPage<Boitier> affecteToEntreprise(Utilisateur userConnected, BoitierEntreprise boitierEntreprise){
		Entreprise entreprise = entrepriseDao.findById(boitierEntreprise.getEntreprise().getId()).get();
		Boitier boitier = findById(boitierEntreprise.getBoitier().getId());
		if (boitier.getEtat() == PairingBox.Paired)
			throw new ErrorException("Ce boitier est affecté à un site !");
		
		// Préparation de l'action pour la traçabilité
		String action = "Affectation de boitier à une entreprise : " + System.lineSeparator()
		+ "Boitier : " + boitier.toString() + System.lineSeparator();
		
		
		boitier.setEntreprise(entreprise);
		boitier = boitierDao.save(boitier);
		
		// Traces du nouveau
		action += "Entreprise : " + boitier.getEntreprise().toString();
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
		
		Pageable pageable = PageRequest.of(0, 20, Sort.by("reference").ascending());
		return findByKeyword(userConnected, "", pageable);
		
	}

	@Override
	public List<Boitier> findAllByEntreprise(Entreprise entreprise) {
		return boitierDao.findByEntreprise(entreprise);
	}

	@Override
	public List<Boitier> findAll() {
		List<Boitier> list = new ArrayList<>();
		boitierDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public void delete(Utilisateur userConnected,long id) {
		
		Boitier boitier =  findById(id);
		// Préparation de l'action pour la traçabilité
		String action = "Suppression boitier : " + System.lineSeparator() + boitier.toString();
		
		boitierDao.deleteById(id);
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
	}

	@Override
	public Boitier findById(Long id) {
		return boitierDao.findById(id).get();
	}
	
	@Override
	public Boitier findByImei(String imei) {
		return boitierDao.findByImei(imei);
	}
	
	@Override
	public EntityPage<Boitier> findByKeyword(Utilisateur userConnected, String keyword, Pageable pageable) {
			Page<Boitier> boitiersPage = null ;
			
			if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
				boitiersPage = boitierDao.findByReferenceContainingOrImeiContaining(
						keyword, keyword, pageable);
			} else {
				boitiersPage = boitierDao.findByKeywordAndEntreprise(keyword, userConnected.getEntreprise().getId(), pageable);
			}
			
			EntityPage<Boitier> boitiers = new EntityPage<Boitier>();

			boitiers.setList(boitiersPage.getContent());

			PageUtil pageUtil = new PageUtil();
			pageUtil.setNombreElementParPage(boitiersPage.getNumberOfElements());
			pageUtil.setNombrePage(boitiersPage.getTotalPages());
			pageUtil.setNumeroPage(boitiersPage.getNumber() + 1);
			pageUtil.setNombreTotalElement(boitiersPage.getTotalElements());

			boitiers.setPageUtil(pageUtil);

			return boitiers;
		
	}
	
	@Override
	public EntityPage<Boitier> findByKeywordAndEtat(Utilisateur userConnected, String keyword, PairingBox etat, Pageable pageable) {
			Page<Boitier> boitiersPage = null ;
			
			if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
				System.out.println("----------etat : " + etat + "   /    recherche : " + keyword);
				boitiersPage = boitierDao.findByKeywordAndEtat(
						keyword, etat, pageable);
				System.out.println("----------count : " + boitiersPage.getContent().size());
			} else {
				boitiersPage = boitierDao.findByKeywordAndEntrepriseAndEtat(keyword, etat, userConnected.getEntreprise().getId(), pageable);
			}
			
			EntityPage<Boitier> boitiers = new EntityPage<Boitier>();

			boitiers.setList(boitiersPage.getContent());

			PageUtil pageUtil = new PageUtil();
			pageUtil.setNombreElementParPage(boitiersPage.getNumberOfElements());
			pageUtil.setNombrePage(boitiersPage.getTotalPages());
			pageUtil.setNumeroPage(boitiersPage.getNumber() + 1);
			pageUtil.setNombreTotalElement(boitiersPage.getTotalElements());

			boitiers.setPageUtil(pageUtil);

			return boitiers;
		
	}


}
