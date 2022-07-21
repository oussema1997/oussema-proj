package com.sfm.obd.controller;

import com.sfm.obd.dao.EntrepriseClienteDao;
import com.sfm.obd.model.EntrepriseCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sfm.obd.dto.ApiResponse;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.EntrepriseDto;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.EntrepriseService;
import com.sfm.obd.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/entreprise")
public class EntrepriseController {
	
	@Autowired
	private EntrepriseService entrepriseService;

	@Autowired
	private EntrepriseClienteDao entrepriseClienteDao;
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ApiResponse saveCategorie(@RequestBody EntrepriseDto entreprise) {
		//if (!isSuperAdmin()) throw new NotAuthorizedException();
		if(isSuperAdmin()){
			Utilisateur userConnected = getConnectedUser();
			Entreprise retour =  entrepriseService.save(userConnected,entreprise);
			return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		} else if(isAdmin()){
			Utilisateur userConnected = getConnectedUser();
			EntrepriseCliente retour =  entrepriseService.saveEntrepriseCliente(userConnected,entreprise);
			return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		}

		return new ApiResponse( new NotAuthorizedException(), "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ApiResponse listCategorie(
			@RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
			@RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
			@RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {
		
		//if (!isSuperAdmin()) throw new NotAuthorizedException();
		EntityPage<?>  retour = new EntityPage<>();

		if(isSuperAdmin()){
			Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("nom").ascending());

			  retour = entrepriseService.findByKeyword(recherche, pageable);
		} else if (isAdmin()){
			Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("nom").ascending());

			  retour = entrepriseService.findByEntrepriseIdAndNomContaining(getConnectedUser().getEntreprise().getId(),recherche, pageable);
		}
		

		
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}
	
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public ApiResponse getById(@RequestBody long id) {
		
		if (! isSuperAdmin()) throw new NotAuthorizedException();
		
		Entreprise retour =   entrepriseService.findById(id);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ApiResponse deleteById(@RequestBody long id) {
		
		if (! isSuperAdmin()) throw new NotAuthorizedException();
		Utilisateur userConnected = getConnectedUser();
		entrepriseService.delete(userConnected,id);
		return new ApiResponse("", "OK", HttpStatus.OK.value());
	}
	
	public boolean isSuperAdmin() {
		Utilisateur userConnected = getConnectedUser();
		if (userConnected.getRole()==UserRoles.SuperAdministrateur) {
			return true;
		}
		return false;
	}
	public boolean isAdmin() {
		Utilisateur userConnected = getConnectedUser();
		if (userConnected.getRole()==UserRoles.Administrateur) {
			return true;
		}
		return false;
	}
	
	private Utilisateur getConnectedUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
		String email = userDetails.getUsername();
		return userService.findByEmail(email);
	}
	
}
