package com.sfm.obd.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "voiture", "user"}))
public class SuiviVoiture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private Utilisateur user;

    @ManyToOne
    @JoinColumn(name = "voiture")
    private Voiture voiture;

    public SuiviVoiture(Utilisateur user, Voiture voiture) {
        super();
        this.user = user;
        this.voiture = voiture;
    }
}
