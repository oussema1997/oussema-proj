package com.sfm.obd.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.sfm.obd.enumer.TypeEntreprise;
import com.sfm.obd.enumer.UserRoles;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Entreprise {

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
	
	@JsonIgnoreProperties({"entreprise"})
	@OneToOne
	@JoinColumn(name = "gestionnaire")
	private Utilisateur gestionnaire;
	
	public Entreprise(long id, String nom, String logo) {
		super();
		this.id = id;
		this.nom = nom;
		this.logo = logo;
	}
	
	@Override
	public String toString() {
		return nom + " ["
				+ ", logo=" + logo
				+ ", gestionnaire = " + gestionnaire.getFirstname() +" " + gestionnaire.getFirstname()
					+ "["
					+ "email=" + gestionnaire.getEmail()
					+ ", téléphone=" + gestionnaire.getPhone()
					+ "]"
				+ "]";
	}

}
