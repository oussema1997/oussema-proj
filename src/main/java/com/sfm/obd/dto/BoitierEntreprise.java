package com.sfm.obd.dto;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Entreprise;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoitierEntreprise {
	private Boitier boitier;
	private Entreprise entreprise;

	public BoitierEntreprise(Boitier boitier, Entreprise entreprise) {
		super();
		this.boitier = boitier;
		this.entreprise = entreprise;
	}

}
