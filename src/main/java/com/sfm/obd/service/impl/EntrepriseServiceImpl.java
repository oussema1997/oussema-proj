package com.sfm.obd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sfm.obd.dao.EntrepriseClienteDao;
import com.sfm.obd.enumer.TypeEntreprise;
import com.sfm.obd.model.EntrepriseCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sfm.obd.dao.EntrepriseDao;
import com.sfm.obd.dao.TraceDao;
import com.sfm.obd.dao.UserDao;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.EntrepriseDto;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.dto.UserDto;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.EntityNotFoundException;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Trace;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.EntrepriseService;


@Service(value = "entrepriseService")
public class EntrepriseServiceImpl implements EntrepriseService {
	
	@Autowired
	private EntrepriseDao entrepriseDao;
	@Autowired
	private EntrepriseClienteDao entrepriseClienteDao;
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TraceDao traceDao;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;

	@Override
	public Entreprise save(Utilisateur userConnected,EntrepriseDto entrepriseDto) {
		
		
		// Préparation de l'action pour la traçabilité
		String action;
		if(entrepriseDto.getId() == 0) {
    		action = "Ajout entreprise : " + System.lineSeparator();
    	} else {
    		action = "Modification entreprise : " +  System.lineSeparator();
    		Entreprise oldEntreprise = entrepriseDao.findById(entrepriseDto.getId()).get();
    		// Traces de l'ancien
			action += oldEntreprise.toString() + " ==> " + System.lineSeparator();
    	}			
		
		
		UserDto gestionnaire = entrepriseDto.getGestionnaire();
		
		//Créer l'entreprise
		Entreprise entreprise = new Entreprise(entrepriseDto.getId(), entrepriseDto.getNom(), entrepriseDto.getLogo());
		entreprise = entrepriseDao.save(entreprise);
		
		//Créer l'utilisateur
		Utilisateur user;
		
		if (gestionnaire.getId() == 0) { // Nouvel utilisateur
			user = new Utilisateur(gestionnaire);
			user.setPassword(bcryptEncoder.encode(gestionnaire.getPassword()));
		} else {
			// Utilisateur existant
			user = userDao.findById(gestionnaire.getId()).get();
			String password = user.getPassword();
			user = new Utilisateur(gestionnaire);
			user.setPassword(password);
		}
		user.setEntreprise(entreprise);
		user.setRole(UserRoles.Administrateur);
		user = userDao.save(user);
		
		// Spécifier que le gestionnaire est également cet utilisateur
		entreprise.setGestionnaire(user);
		entreprise = entrepriseDao.save(entreprise);
		
		entrepriseDao.refresh(entreprise);
		
		// Traces du nouveau
		action += entreprise.toString();
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
		
		return entreprise;
	}
	
	@Override
	public List<Entreprise> findAll() {
		List<Entreprise> list = new ArrayList<>();
		entrepriseDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	@Override
	public void delete(Utilisateur userConnected,long id) {
		Entreprise entreprise = findById(id);
		
		// Préparation de l'action pour la traçabilité
		String action = "Suppression entreprise : " + System.lineSeparator() + entreprise.toString();
		
		entrepriseDao.deleteById(id);
		
		// Sauvegarder la trace
		traceDao.save(new Trace(new Date(), action, userConnected));
	}
	
	@Override
	public Entreprise findById(Long id) {
		Entreprise entreprise =  entrepriseDao.findById(id)
				.orElseGet(() -> entrepriseDao.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Entreprise introuvable")));
		
		return entreprise;
	}
	
	@Override
	public EntityPage<Entreprise> findByKeyword(String keyword, Pageable pageable) {
		
		Page<Entreprise> entreprisesPage = entrepriseDao.findByNomContaining(keyword,pageable);

		EntityPage<Entreprise> entreprises = new EntityPage<Entreprise>();

		entreprises.setList(entreprisesPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(entreprisesPage.getNumberOfElements());
		pageUtil.setNombrePage(entreprisesPage.getTotalPages());
		pageUtil.setNumeroPage(entreprisesPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(entreprisesPage.getTotalElements());

		entreprises.setPageUtil(pageUtil);

		return entreprises;
	}

	@Override
	public EntityPage<EntrepriseCliente> findByEntrepriseIdAndNomContaining(long entrepriseId, String keyword, Pageable pageable) {
		Page<EntrepriseCliente> entreprisesPage = entrepriseClienteDao.findByEntrepriseIdAndNomContaining(entrepriseId,keyword,pageable);

		EntityPage<EntrepriseCliente> entreprises = new EntityPage<EntrepriseCliente>();

		entreprises.setList(entreprisesPage.getContent());

		PageUtil pageUtil = new PageUtil();
		pageUtil.setNombreElementParPage(entreprisesPage.getNumberOfElements());
		pageUtil.setNombrePage(entreprisesPage.getTotalPages());
		pageUtil.setNumeroPage(entreprisesPage.getNumber() + 1);
		pageUtil.setNombreTotalElement(entreprisesPage.getTotalElements());

		entreprises.setPageUtil(pageUtil);

		return entreprises;
	}

	@Override
	public EntrepriseCliente saveEntrepriseCliente(Utilisateur userConnected, EntrepriseDto entrepriseDto) {
		UserDto gestionnaire = entrepriseDto.getGestionnaire();

		//Créer l'entreprise
		EntrepriseCliente entrepriseCliente = new EntrepriseCliente(entrepriseDto.getId(), entrepriseDto.getNom(), entrepriseDto.getLogo());
		entrepriseCliente = entrepriseClienteDao.save(entrepriseCliente);

		//Créer l'utilisateur
		Utilisateur user;

		if (gestionnaire.getId() == 0) { // Nouvel utilisateur
			user = new Utilisateur(gestionnaire);
			user.setPassword(bcryptEncoder.encode(gestionnaire.getPassword()));
		} else {
			// Utilisateur existant
			user = userDao.findById(gestionnaire.getId()).get();
			String password = user.getPassword();
			user = new Utilisateur(gestionnaire);
			user.setPassword(password);
		}
		user.setEntrepriseCliente(entrepriseCliente);
		user.setRole(UserRoles.Locataire);
		user = userDao.save(user);

		// Spécifier que le gestionnaire est également cet utilisateur
		entrepriseCliente.setTypeEntreprise(TypeEntreprise.Cliente);
		entrepriseCliente.setEntreprise(userConnected.getEntreprise());
		entrepriseCliente.setGestionnaire(user);
		entrepriseCliente = entrepriseClienteDao.save(entrepriseCliente);

		entrepriseClienteDao.refresh(entrepriseCliente);



		return entrepriseCliente;
	}


}
