package com.sfm.obd.dto.mesures;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MesureDTO {
	private String val1;
	private String val2;
	private String val3;
	private String val4;
	private String val5;
	private long indicateur;

	public MesureDTO(String val1, String val2, String val3, long indicateur) {
		super();
		this.val1 = val1;
		this.val2 = val2;
		this.val3 = val3;
		this.indicateur = indicateur;
	}


}
