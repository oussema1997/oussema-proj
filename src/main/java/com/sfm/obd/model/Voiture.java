package com.sfm.obd.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Voiture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String matricule;

    @Column()
    private String marque;

    @Column()
    private String model;

    @Column()
    private String annee;

    @Column()
    private double longitude;

    @Column()
    private double latitude;

    @ManyToOne
    @JoinColumn(name = "entreprise")
    private Entreprise entreprise;

    //@JsonIgnoreProperties({"site", "entreprise"})
    //@LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne
    @JoinColumn(name = "boitier")
    private Boitier boitier;

    @ManyToOne
    @JoinColumn(name = "entrepriseCliente")
    private EntrepriseCliente entrepriseCliente;

    @JsonIgnore
    @OneToMany(mappedBy = "voiture", cascade = CascadeType.REMOVE)
    private List<TimelineBox> timelineBoxes;

    @Override
    public String toString() {
        return matricule + " ["
                + "entreprise=" + entreprise.getNom()
                + "longitude=" + longitude
                + "latitude=" + latitude
                + "marque=" + marque
                + "model=" + model
                + "]";
    }

}
