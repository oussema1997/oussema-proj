package com.sfm.obd.dao;

import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.EntrepriseCliente;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.myrepo.MyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepriseClienteDao extends MyRepository<EntrepriseCliente, Long> {

    Page<EntrepriseCliente> findByEntrepriseIdAndNomContaining(long entrepriseId,String keyword, Pageable pageable);

    List<EntrepriseCliente> findByEntrepriseId(long entrepriseId);

    List<EntrepriseCliente> findByEntrepriseGestionnaire(Utilisateur connectedUser);
}
