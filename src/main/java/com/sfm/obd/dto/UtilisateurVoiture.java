package com.sfm.obd.dto;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Utilisateur;
import com.sfm.obd.model.Voiture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UtilisateurVoiture {
    private Utilisateur utilisateur;
    private Voiture voiture;


    public UtilisateurVoiture(Utilisateur utilisateur, Voiture voiture) {
        super();
        this.utilisateur = utilisateur;
        this.voiture = voiture;

    }

}