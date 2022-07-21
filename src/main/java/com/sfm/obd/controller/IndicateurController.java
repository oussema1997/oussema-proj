package com.sfm.obd.controller;

import com.sfm.obd.model.Parametre;
import com.sfm.obd.service.ParametreService;
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
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Indicateur;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.IndicateurService;
import com.sfm.obd.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/indicateur")
public class IndicateurController {
	
	@Autowired
	private IndicateurService indicateurService;

	@Autowired
	private ParametreService parametreService;


	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ApiResponse saveCategorie(@RequestBody Indicateur indicateur) {
		if (!isSuperAdmin()) throw new NotAuthorizedException();
		
		Utilisateur userConnected = getConnectedUser();
		Indicateur retour =  indicateurService.save(userConnected,indicateur);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ApiResponse listCategorie(
			@RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
			@RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
			@RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {
		
		if (! isSuperAdmin()) throw new NotAuthorizedException();
		
		Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("libelle").ascending());
		
		EntityPage<Indicateur>  retour = indicateurService.findByKeyword(recherche, pageable);
		
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}


	
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public ApiResponse getById(@RequestBody long id) {
		Indicateur retour =   indicateurService.findById(id);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ApiResponse deleteById(@RequestBody long id) {
		if (! isSuperAdmin()) throw new NotAuthorizedException();
		Utilisateur userConnected = getConnectedUser();
		indicateurService.delete(userConnected,id);
		return new ApiResponse("", "OK", HttpStatus.OK.value());
	}
	
	public boolean isSuperAdmin() {
		Utilisateur userConnected = getConnectedUser();
		if (userConnected.getRole()==UserRoles.SuperAdministrateur) {
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
