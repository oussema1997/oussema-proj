package com.sfm.obd.dto;

import com.sfm.obd.model.Boitier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoitierEtat {

	Boitier boitier;
	String etat;

	public BoitierEtat(Boitier boitier, String etat) {
		super();
		this.boitier = boitier;
		this.etat = etat;
	}

}
