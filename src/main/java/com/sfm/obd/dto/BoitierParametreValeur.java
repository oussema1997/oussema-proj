package com.sfm.obd.dto;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Parametre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoitierParametreValeur {
	private Boitier boitier;
	private Parametre parametre;
	private String valeur;


	public BoitierParametreValeur(Boitier boitier, Parametre parametre, String valeur) {
		super();
		this.boitier = boitier;
		this.parametre = parametre;
		this.valeur = valeur;
	}

}
