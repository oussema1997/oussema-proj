package com.sfm.obd.controller;

import com.sfm.obd.dao.BoitierDao;
import com.sfm.obd.dao.EntrepriseClienteDao;
import com.sfm.obd.dao.EntrepriseDao;
import com.sfm.obd.dao.UserDao;
import com.sfm.obd.dto.*;
import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.EntrepriseCliente;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.model.Voiture;

import com.sfm.obd.service.BoitierService;
import com.sfm.obd.service.EntrepriseService;
import com.sfm.obd.service.UserService;
import com.sfm.obd.service.VoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/voiture")
public class VoitureController {

    @Autowired
    private UserService userService;

    @Autowired
    private VoitureService voitureService;

    @Autowired
    private BoitierService boitierService;

    @Autowired
    private EntrepriseClienteDao entrepriseClienteDao;

    @Autowired
    private UserDao userDao;



    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ApiResponse saveVoiture(@RequestBody Voiture voiture) {

        if (!isAdmin()) throw new NotAuthorizedException();

        Utilisateur userConnected = getConnectedUser();
        Voiture retour =  voitureService.save(userConnected, voiture);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ApiResponse listVoiture(
            @RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
            @RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
            @RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {

        Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("matricule").ascending());

        Utilisateur userConnected = getConnectedUser();
        EntityPage<Voiture> retour = voitureService.findByKeyword(userConnected,recherche,recherche,recherche, pageable);

        return new ApiResponse(retour, "OK", HttpStatus.OK.value());

    }

    @RequestMapping(value = "/filteredVoiture", method = RequestMethod.POST)
    public ApiResponse listFilteredVoiture(
            @RequestParam(name = "numPage", defaultValue = "1", required = false) int numPage,
            @RequestParam(name = "nombreElement", defaultValue = "20", required = false) int nombreElement,
            @RequestParam(name = "selectedVoitures", required = false) String marque,
            @RequestBody EntrepriseCliente entrepriseCliente) {

        Pageable pageable = PageRequest.of(numPage - 1, nombreElement, Sort.by("marque").ascending());
        System.out.println(marque +"  -----   "+ entrepriseCliente);
        EntityPage<Voiture> retour = voitureService.filterVoiture(marque, entrepriseCliente, pageable);

        return new ApiResponse(retour, "OK", HttpStatus.OK.value());

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ApiResponse deleteById(@RequestBody long id) {

        if (!isAdmin() && !isSuperAdmin()) throw new NotAuthorizedException();

        Utilisateur userConnected = getConnectedUser();
        voitureService.delete(userConnected,id);
        return new ApiResponse("", "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public ApiResponse getById(@RequestBody long id) {
        Voiture retour =   voitureService.findById(id);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/followers", method = RequestMethod.POST)
    public ApiResponse followers(@RequestBody Voiture voiture,
                                 @RequestParam(name = "recherche", defaultValue = "", required = false) String recherche) {
        if (!isAdmin()) throw new NotAuthorizedException();
        Utilisateur userConnected = getConnectedUser();
        List<Utilisateur> retour =  voitureService.followers(userConnected, voiture,recherche);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/unfollowClient", method = RequestMethod.POST)
    public Voiture unfollowClient(@RequestBody Voiture voiture) {
        Voiture retour = voitureService.unfollowClient(voiture);

        return retour;
    }

    @RequestMapping(value = "/affectClient", method = RequestMethod.POST)
    public Utilisateur affectClientToVoiture(@RequestBody UtilisateurVoiture utilisateurVoiture) {
        Utilisateur retour = voitureService.affectClientToVoiture(utilisateurVoiture.getUtilisateur(), utilisateurVoiture.getVoiture().getId());

        return retour;
    }

    @RequestMapping(value = "/entrepriseCliente", method = RequestMethod.POST)
    public ApiResponse getEntrepriseCliente(@RequestBody long idVoiture) {
        if (!isAdmin()) throw new NotAuthorizedException();
        EntrepriseCliente retour =  voitureService.getEntrepriseCliente(idVoiture);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/listEntrepriseCliente", method = RequestMethod.GET)
    public ApiResponse getListEntrepriseCliente() {
        Utilisateur userConnected = getConnectedUser();
        List<EntrepriseCliente> retour =  voitureService.listEntrepriseCliente(userConnected);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/pairing", method = RequestMethod.POST)
    public ApiResponse pairing(@RequestBody VoitureBoitier voitureBoitier) {
        Utilisateur userConnected = getConnectedUser();
        EntityPage<Voiture> retour = voitureService.pairingVoiture(userConnected, voitureBoitier);
        return new ApiResponse(retour, "OK", HttpStatus.OK.value());
    }

    @RequestMapping(value = "/countEntrepriseNbr", method = RequestMethod.GET)
    public CountDashboard countNbrEntreprise() {
        Utilisateur userConnected = getConnectedUser();
        long nbrVoitures =  voitureService.findAllByEntreprise(userConnected.getEntreprise()).stream().count();
        long nbrVoituresFree =  voitureService.findAllByEntreprise(userConnected.getEntreprise()).stream().filter(x->x.getEntrepriseCliente() == null).count();
        long nbrBoxes =  boitierService.findAllByEntreprise(userConnected.getEntreprise()).stream().count();
        long nbrBoxesUnpaired =  boitierService.findAllByEntreprise(userConnected.getEntreprise()).stream().filter(x->x.getEtat() == PairingBox.Unpaired).count();
        long nbrEntrepriseCliente = entrepriseClienteDao.findByEntrepriseId(userConnected.getEntreprise().getId()).stream().count();
        long nbrUsers = userDao.findByEntreprise(userConnected.getEntreprise()).stream().count();
        CountDashboard countDashboard = new CountDashboard(nbrVoitures, nbrVoituresFree, nbrBoxes, nbrBoxesUnpaired, nbrEntrepriseCliente, nbrUsers);



        return countDashboard;
    }

    public boolean isSuperAdmin() {
        Utilisateur userConnected = getConnectedUser();
        if (userConnected.getRole()== UserRoles.SuperAdministrateur) {
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
