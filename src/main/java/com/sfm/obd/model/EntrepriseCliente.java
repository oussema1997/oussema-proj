package com.sfm.obd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sfm.obd.enumer.TypeEntreprise;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class EntrepriseCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String nom;

    @Column
    private String logo;

    @Enumerated(EnumType.STRING)
    @Column
    private TypeEntreprise typeEntreprise;

    @JsonIgnoreProperties({"entreprise","entrepriseCliente"})
    @OneToOne
    @JoinColumn(name = "gestionnaire")
    private Utilisateur gestionnaire;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "loueur")
    private Entreprise entreprise;

    public EntrepriseCliente(long id, String nom, String logo) {
        super();
        this.id = id;
        this.nom = nom;
        this.logo = logo;
    }

}
