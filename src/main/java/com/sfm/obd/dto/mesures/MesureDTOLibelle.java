package com.sfm.obd.dto.mesures;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MesureDTOLibelle {
	private String val1;
	private String val2;
	private String val3;
	private long indicateur;
	private String libelle;
	
	public MesureDTOLibelle(String val1, String val2, String val3, long indicateur, String libelle) {
		super();
		this.val1 = val1;
		this.val2 = val2;
		this.val3 = val3;
		this.indicateur = indicateur;
		this.libelle = libelle;
	}

}
