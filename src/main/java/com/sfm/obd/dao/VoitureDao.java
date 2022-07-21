package com.sfm.obd.dao;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.EntrepriseCliente;
import com.sfm.obd.model.Voiture;
import com.sfm.obd.myrepo.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoitureDao extends MyRepository<Voiture, Long> {
    Page<Voiture> findByMatriculeContainingOrMarqueContainingOrModelContaining(String keyword1, String keyword2, String keyword3, Pageable pageable);
    Voiture findVoitureByBoitier(Boitier boitier);

    Page<Voiture> findByMarqueAndEntrepriseCliente(String marque, EntrepriseCliente entrepriseCliente, Pageable pageable);

    List<Voiture> findByEntreprise(Entreprise entreprise);
    long countByEntreprise(Entreprise entreprise);

    //Page<Voiture> findByEntrepriseAndNomSiteContaining(Entreprise entreprise, String keyword, Pageable pageable);
}
