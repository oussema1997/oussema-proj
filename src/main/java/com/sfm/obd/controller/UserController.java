package com.sfm.obd.controller;

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
import com.sfm.obd.dto.UserDto;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.ErrorException;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ApiResponse saveUser(@RequestBody UserDto userDto) {
		Utilisateur userConnected = getConnectedUser();
		Utilisateur retour = userService.save(userDto, userConnected);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ApiResponse listUser(@RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
			@RequestParam(name = "nombreElement", defaultValue = "100", required = false) int nombreElement,
			@RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {
		
		if ((!isSuperAdmin()) && (!isAdmin()))
			throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
		
		Utilisateur userConnected = getConnectedUser();
				
		Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("lastname").ascending());
		
		EntityPage<Utilisateur> retour = userService.findByNomContaining(userConnected, recherche, pageable);
		
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}
	
	@RequestMapping(value = "/listUserByRole", method = RequestMethod.GET)
	public ApiResponse listUserByRole(
			@RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
			@RequestParam(name = "nombreElement", defaultValue = "100", required = false) int nombreElement,
			@RequestParam(name = "role", required = true) UserRoles role,
			@RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {
		
		if ((!isSuperAdmin()) && (!isAdmin()))
			throw new ErrorException("Vous n'êtes pas autorisés à effectuer cette action");
		
		Utilisateur userConnected = getConnectedUser();
		
		Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("lastname").ascending());
		EntityPage<Utilisateur> retour = userService.findByRoleAndKeyword(userConnected, role, recherche, pageable);
		
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
		
	}

	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public ApiResponse getById(@RequestBody long id) {
		Utilisateur userConnected = getConnectedUser();
		Utilisateur retour = userService.findById(userConnected,id);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ApiResponse deleteById(@RequestBody long id) {
		Utilisateur userConnected = getConnectedUser();
		userService.delete(userConnected,id);
		return new ApiResponse("", "OK", HttpStatus.OK.value());
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ApiResponse changePassword(
			@RequestBody UserDto userDTO) {
		Utilisateur userConnected = getConnectedUser();
		Utilisateur usertoChange = new Utilisateur(userDTO);
		Utilisateur retour = userService.changePassword(userConnected, usertoChange);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
	}
	
	@RequestMapping(value = "/enable", method = RequestMethod.POST)
	public ApiResponse enable(
			@RequestBody Utilisateur user) {
		Utilisateur userConnected = getConnectedUser();
		Utilisateur retour = userService.enable(userConnected, user);
		return new ApiResponse(retour, "OK", HttpStatus.OK.value());
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
