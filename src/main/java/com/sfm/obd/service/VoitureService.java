package com.sfm.obd.service;

import com.sfm.obd.dto.EntityPage;
import com.sfm.obd.dto.VoitureAllUsers;
import com.sfm.obd.dto.VoitureBoitier;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.EntrepriseCliente;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.model.Voiture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoitureService {

    Voiture save(Utilisateur userConnected, Voiture voiture);

    List<Voiture> findAll();

    void delete(Utilisateur userConnected,long id);

    Voiture findById(Long id);

    EntityPage<Voiture> findByKeyword(Utilisateur userConnected, String keyword1,String keyword2,String keyword3, Pageable pageable);

    List<Utilisateur> followers(Utilisateur userConnected, Voiture voiture, String recherche);

    EntityPage<Voiture> pairingVoiture(Utilisateur userConnected, VoitureBoitier voitureBoitier);

    Utilisateur affectClientToVoiture(Utilisateur user, long idVoiture);

    Voiture unfollowClient(Voiture voiture);

    EntrepriseCliente getEntrepriseCliente(long idVoiture);

    List<EntrepriseCliente> listEntrepriseCliente(Utilisateur userConnected);

    EntityPage<Voiture> filterVoiture(String marque, EntrepriseCliente entrepriseCliente, Pageable pageable);

    List<Voiture> findAllByEntreprise(Entreprise entreprise);
}
