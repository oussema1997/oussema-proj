package com.sfm.obd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Erreur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String codeErreur;

    @Column
    private String erreur;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mesure")
    private Mesure mesure;

    public Erreur(String codeErreur, String erreur, Mesure mesure) {
        super();
        this.codeErreur = codeErreur;
        this.erreur = erreur;
        this.mesure = mesure;
    }


}
