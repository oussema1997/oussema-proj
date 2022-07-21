package com.sfm.obd.dto.mesures;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LastMesure {
	
	private EtatDTO etat;
	private List<MesureDTOLibelle> mesures = new ArrayList<>();

	public LastMesure(EtatDTO etat, List<MesureDTOLibelle> mesures) {
		super();
		this.etat = etat;
		this.mesures = mesures;
	}

}
