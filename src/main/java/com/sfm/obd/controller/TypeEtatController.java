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
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.TypeEtat;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.service.TypeEtatService;
import com.sfm.obd.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/typeEtat")
public class TypeEtatController {
    @Autowired
    private TypeEtatService typeEtatService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ApiResponse saveTypeEtat(@RequestBody TypeEtat typeEtat) {
        if (!isSuperAdmin()) throw new NotAuthorizedException();
        Utilisateur userConnected = getConnectedUser();
        TypeEtat retour =  typeEtatService.save(userConnected, typeEtat);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResponse listTypeEtat(
            @RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
            @RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
            @RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {

        if (! isSuperAdmin()) throw new NotAuthorizedException();

        Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("id").ascending());

        EntityPage<TypeEtat> retour = typeEtatService.findByKeyword(recherche, pageable);

        return new ApiResponse(retour, "OK", HttpStatus.OK.value());

    }

    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ApiResponse getById(@RequestBody long id) {
        TypeEtat retour =   typeEtatService.findById(id);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ApiResponse deleteById(@RequestBody long id) {
        if (! isSuperAdmin()) throw new NotAuthorizedException();
        Utilisateur userConnected = getConnectedUser();
        typeEtatService.delete(userConnected, id);
        return new ApiResponse("", "OK", HttpStatus.OK.value());
    }

    public boolean isSuperAdmin() {
        Utilisateur userConnected = getConnectedUser();
        if (userConnected.getRole()== UserRoles.SuperAdministrateur) {
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
