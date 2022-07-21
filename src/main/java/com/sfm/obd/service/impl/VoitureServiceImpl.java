package com.sfm.obd.service.impl;

import com.sfm.obd.dao.*;
import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.PageUtil;
import com.sfm.obd.dto.VoitureAllUsers;
import com.sfm.obd.dto.VoitureBoitier;
import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.enumer.TypeEntreprise;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.exception.ErrorException;
import com.sfm.obd.exception.NotAuthorizedException;
import com.sfm.obd.model.*;
import com.sfm.obd.service.VoitureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "voitureService")
public class VoitureServiceImpl implements VoitureService {

    @Autowired
    private VoitureDao voitureDao;
    @Autowired
    private EntrepriseClienteDao entrepriseClienteDao;
    @Autowired
    private TraceDao traceDao;

    @Autowired
    private SuiviVoitureDao suiviVoitureDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BoitierDao boitierDao;

    @Autowired
    private TimelineBoxDao timelineBoxDao;

    @Override
    public Voiture save(Utilisateur userConnected, Voiture voiture) {
        // Préparation de l'action pour la traçabilité
        String action;
        if(voiture.getId() == 0) {
            action = "Ajout voiture : " + System.lineSeparator();
        } else {
            action = "Modification voiture : " +  System.lineSeparator();
            Voiture oldVoiture = voitureDao.findById(voiture.getId()).get();
            // Traces de l'ancien
            action += oldVoiture.toString() + " ==> " + System.lineSeparator();

            //Garder les boitiers au cas où il y'en aurait
            voiture.setBoitier(oldVoiture.getBoitier());
        }

        System.out.println("dddddddd"+ voiture.getId());
        voiture.setEntreprise(userConnected.getEntreprise());
        voiture = voitureDao.save(voiture);
        voitureDao.refresh(voiture);

        // Traces du nouveau
        action += voitureDao.toString();

        // Sauvegarder la trace
        traceDao.save(new Trace(new Date(), action, userConnected));

        return voiture;
    }

    @Override
    public List<Voiture> findAll() {
        List<Voiture> list = new ArrayList<>();
        voitureDao.findAll().iterator().forEachRemaining(list::add);
        return list;    }

    @Override
    public void delete(Utilisateur userConnected, long id) {
        Voiture voiture = findById(id);

        // Préparation de l'action pour la traçabilité
        String action = "Suppression voiture : " + System.lineSeparator() + voiture.toString();


        voitureDao.deleteById(id);

        // Sauvegarder la trace
        traceDao.save(new Trace(new Date(), action, userConnected));
    }

    @Override
    public Voiture findById(Long id) {
        return voitureDao.findById(id).get();
    }

    @Override
    public EntityPage<Voiture> findByKeyword(Utilisateur userConnected, String keyword1, String keyword2, String keyword3, Pageable pageable) {
        EntityPage<Voiture> voitures = new EntityPage<Voiture>();
        if ((userConnected.getRole() == UserRoles.SuperAdministrateur) ||
                (userConnected.getRole() == UserRoles.Administrateur)) {

            Page<Voiture> voituresPage;

            //if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
            voituresPage = voitureDao.findByMatriculeContainingOrMarqueContainingOrModelContaining(keyword1, keyword2, keyword3, pageable);
            //} else {
            //     voituresPage = voitureDao.findByEntrepriseAndNomSiteContaining(userConnected.getEntreprise(),keyword,pageable);
            // }



            voitures.setList(voituresPage.getContent());

            PageUtil pageUtil = new PageUtil();
            pageUtil.setNombreElementParPage(voituresPage.getNumberOfElements());
            pageUtil.setNombrePage(voituresPage.getTotalPages());
            pageUtil.setNumeroPage(voituresPage.getNumber() + 1);
            pageUtil.setNombreTotalElement(voituresPage.getTotalElements());

            voitures.setPageUtil(pageUtil);

            return voitures;

        }
        return voitures;
    }

    @Override
    public List<Utilisateur> followers(Utilisateur userConnected, Voiture voiture, String recherche) {
        voiture = findById(voiture.getId());

        if (userConnected.getRole() == UserRoles.SuperAdministrateur) {
            //Il a le droit
        } else if (userConnected.getRole() == UserRoles.Administrateur) {
            // S'assurer que le site est de son entreprise
            if (voiture.getEntreprise().getId() != userConnected.getEntreprise().getId()) {
                throw new NotAuthorizedException();
            }
        } else {
            throw new NotAuthorizedException();
        }

        List<Utilisateur> utilisateurs = userDao.findByRole(UserRoles.Locataire);
        utilisateurs = utilisateurs.stream().filter(x-> x.getEntrepriseCliente().getEntreprise() == userConnected.getEntreprise()).collect(Collectors.toList());

        return utilisateurs;
    }

    @Override
    public EntityPage<Voiture> pairingVoiture(Utilisateur userConnected, VoitureBoitier voitureBoitier) {
        Boitier boitier = boitierDao.findById(voitureBoitier.getBoitier().getId()).get();
        Voiture voiture =
                voitureBoitier.isPairing() ? findById(voitureBoitier.getVoiture().getId()) : voitureDao.findVoitureByBoitier(boitier);

        if (userConnected.getRole() == UserRoles.SuperAdministrateur)
            throw new NotAuthorizedException();

        if (userConnected.getRole() == UserRoles.Administrateur) {
            // S'assurer que la voiture et le boitier sont de son entreprise
            if (voiture.getEntreprise().getId() != userConnected.getEntreprise().getId())
                throw new NotAuthorizedException();
            if (boitier.getEntreprise().getId() != userConnected.getEntreprise().getId())
                throw new NotAuthorizedException();
        } else {
            // Vérifier que le wagon est suivi par l'utilisateur connecté
            SuiviVoiture suiviVoiture = suiviVoitureDao.findByUserIdAndVoitureId(userConnected.getId(), voiture.getId());
            if (suiviVoiture == null)
                throw new NotAuthorizedException();

            // Vérifier que le boitier est de son entreprise
            if (boitier.getEntreprise().getId() != userConnected.getEntreprise().getId())
                throw new NotAuthorizedException();
        }

        if(voitureBoitier.isPairing()) { // Demande de pairing

            if (boitier.getEtat() == PairingBox.Paired)
                throw new ErrorException("Ce boitier est déjà rattaché à un site");
            else {
                // Faire le pairing
                // boitier.setVoiture(voiture);
                if(voiture.getBoitier() != null){
                    voiture.getBoitier().setEtat(PairingBox.Unpaired);
                }
                boitier.setEtat(PairingBox.Paired);
                // Mettre à jour la date d'installation du boitier
                boitier.setDateInstallation(new Date());
                boitier = boitierDao.save(boitier);
                boitierDao.refresh(boitier);
                voiture.setBoitier(boitier);
                voitureDao.save(voiture);
                voitureDao.refresh(voiture);


                // Sauvegarder un Timeline du Boitier
                timelineBoxDao.save(
                        new TimelineBox(new Date(), PairingBox.Paired, userConnected, voiture, boitier)
                );

            }
        } else { // Demande d'unpairing

            if (boitier.getEtat() != PairingBox.Paired)
                throw new ErrorException("Ce boitier n'est pas rattaché à un site");
            else {
                // Faire le UnPairing
                //boitier.setLocataire(null);
                boitier.setEtat(PairingBox.Unpaired);
                // Mettre à jour la date d'installation du boitier
                boitier.setDateInstallation(null);
                boitier = boitierDao.save(boitier);
                boitierDao.refresh(boitier);
                voiture.setBoitier(null);
                voitureDao.save(voiture);
                voitureDao.refresh(voiture);

                // Sauvegarder un Timeline du Boitier
                timelineBoxDao.save(
                        new TimelineBox(new Date(), PairingBox.Unpaired, userConnected, voiture, boitier)
                );
            }
        }

        Pageable pageable = PageRequest.of(0, 20, Sort.by("matricule").ascending());

        return findByKeyword(userConnected, "","","", pageable);
    }

    @Override
    public Utilisateur affectClientToVoiture(Utilisateur user, long idVoiture) {
        Voiture voiture = voitureDao.getById(idVoiture);
        voiture.setEntrepriseCliente(user.getEntrepriseCliente());
        voitureDao.save(voiture);
        voitureDao.refresh(voiture);
        return user;
    }

    @Override
    public Voiture unfollowClient(Voiture voiture) {
        voiture = voitureDao.findById(voiture.getId()).get();
        voiture.setEntrepriseCliente(null);
        voitureDao.save(voiture);
        voitureDao.refresh(voiture);
        return voiture;
    }

    @Override
    public EntrepriseCliente getEntrepriseCliente(long idVoiture) {
        Voiture voiture = voitureDao.getById(idVoiture);
        return voiture.getEntrepriseCliente();
    }

    @Override
    public List<EntrepriseCliente> listEntrepriseCliente(Utilisateur userConnected) {

        return entrepriseClienteDao.findByEntrepriseGestionnaire(userConnected);
    }

    @Override
    public EntityPage<Voiture> filterVoiture(String marque, EntrepriseCliente entrepriseCliente, Pageable pageable) {

        EntityPage<Voiture> voitures = new EntityPage<Voiture>();

            Page<Voiture> voituresPage;
            voituresPage = voitureDao.findByMarqueAndEntrepriseCliente(marque, entrepriseCliente, pageable);


            voitures.setList(voituresPage.getContent());

            PageUtil pageUtil = new PageUtil();
            pageUtil.setNombreElementParPage(voituresPage.getNumberOfElements());
            pageUtil.setNombrePage(voituresPage.getTotalPages());
            pageUtil.setNumeroPage(voituresPage.getNumber() + 1);
            pageUtil.setNombreTotalElement(voituresPage.getTotalElements());

            voitures.setPageUtil(pageUtil);

        return voitures ;
    }

    @Override
    public List<Voiture> findAllByEntreprise(Entreprise entreprise) {

        return voitureDao.findByEntreprise(entreprise);
    }
}
