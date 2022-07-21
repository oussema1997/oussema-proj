package com.sfm.obd.dto.mesures;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EtatDTO {
	
	private long id;
	private String libelle;

	public EtatDTO(long id,String libelle) {
		super();
		this.libelle = libelle;
		this.id = id;
	}

}
