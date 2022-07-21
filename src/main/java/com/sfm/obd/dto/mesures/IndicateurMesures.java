package com.sfm.obd.dto.mesures;

import java.util.ArrayList;
import java.util.List;

import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Indicateur;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IndicateurMesures {

	private Boitier boitier;
	private Indicateur indicateur;
	private List<MesureDTOVal> mesures = new ArrayList<>();

	public IndicateurMesures(Boitier boitier, Indicateur indicateur, List<MesureDTOVal> mesures) {
		super();
		this.indicateur = indicateur;
		this.mesures = mesures;
		this.boitier = boitier;
	}

}
