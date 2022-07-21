package com.sfm.obd.dto;

import com.sfm.obd.model.Boitier;

import com.sfm.obd.model.Voiture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoitureBoitier {
    private Voiture voiture;
    private Boitier boitier;
    private boolean pairing;

    public VoitureBoitier(Voiture voiture, Boitier boitier, boolean pairing) {
        super();
        this.voiture = voiture;
        this.boitier = boitier;
        this.pairing = pairing;
    }

}
