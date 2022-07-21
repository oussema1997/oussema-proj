package com.sfm.obd.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Parametre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private String attribut;

	@Column
	private String valeurParDefaut;
	
	@Column
	private String unite;

	@Override
	public String toString() {
		return attribut + " ["
				+ "valeur par défaut =" + valeurParDefaut
				+ ", unité =" + unite
				+ "]";
	}

}
