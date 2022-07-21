package com.sfm.obd.dto;

import com.sfm.obd.model.Boitier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountDashboard {

    long nbrVoiture;
    long nbrVoitureFree;
    long nbrboxes;
    long nbrboxesUnpaired;
    long nbrEntrepriseCliente;
    long nbrUsers;

    public CountDashboard(long nbrVoiture, long nbrVoitureFree, long nbrboxes, long nbrboxesUnpaired, long nbrEntrepriseCliente, long nbrUsers) {
        super();
        this.nbrVoiture = nbrVoiture;
        this.nbrVoitureFree = nbrVoitureFree;
        this.nbrboxes = nbrboxes;
        this.nbrboxesUnpaired = nbrboxesUnpaired;
        this.nbrEntrepriseCliente = nbrEntrepriseCliente;
        this.nbrUsers = nbrUsers;

    }
}
