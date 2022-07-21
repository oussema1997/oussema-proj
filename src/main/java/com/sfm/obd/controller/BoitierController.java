package com.sfm.obd.controller;

import java.util.List;

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
import com.sfm.obd.dto.BoitierEntreprise;
import com.sfm.obd.dto.BoitierParametreValeur;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.model.ValeurParametre;
import com.sfm.obd.service.BoitierService;
import com.sfm.obd.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/boitier")
public class BoitierController {
	
	@Autowired
	private BoitierService boitierService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ApiResponse listBoitier(
			@RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
			@RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
			@RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {
		
		Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("reference").ascending());
		
		Utilisateur userConnected = getConnectedUser();
		EntityPage<Boitier>  retour = boitierService.findByKeyword(userConnected,recherche, pageable);
		
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}
	
	@RequestMapping(value = "/listByEtat", method = RequestMethod.GET)
	public ApiResponse listBoitierByEtat(
			@RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
			@RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
			@RequestParam(name = "recherche", defaultValue = "", required = false) String recherche,
			@RequestParam(name = "etat", required = true) PairingBox etat) {
		
		Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("reference").ascending());
		
		Utilisateur userConnected = getConnectedUser();
		EntityPage<Boitier>  retour = boitierService.findByKeywordAndEtat(userConnected,recherche, etat, pageable);
		
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}
	
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public ApiResponse getById(@RequestBody long id) {
		Boitier retour =   boitierService.findById(id);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ApiResponse deleteById(@RequestBody long id) {
		if (!isSuperAdmin()) throw new NotAuthorizedException();
		Utilisateur userConnected = getConnectedUser();
		boitierService.delete(userConnected,id);
		return new ApiResponse("", "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/parametres", method = RequestMethod.POST)
	public ApiResponse getBoitierParams(@RequestBody Boitier boitier) {
		Utilisateur userConnected = getConnectedUser();
		List<ValeurParametre> retour =   boitierService.findBoitierParametre(userConnected,boitier);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/updateParametre", method = RequestMethod.POST)
	public ApiResponse getBoitierParams(@RequestBody BoitierParametreValeur boitierParametreValeur ) {
		Utilisateur userConnected = getConnectedUser();
		List<ValeurParametre> retour =   boitierService.updateBoitierParametre(userConnected, boitierParametreValeur);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/affecteToEntreprise", method = RequestMethod.POST)
	public ApiResponse affecteToEntreprise(@RequestBody BoitierEntreprise boitierEntreprise) {
		if (!isSuperAdmin()) throw new NotAuthorizedException();
		Utilisateur userConnected = getConnectedUser();
		EntityPage<Boitier> retour = boitierService.affecteToEntreprise(userConnected, boitierEntreprise);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
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
