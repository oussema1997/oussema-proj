package com.sfm.obd.dto.mesures;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MesureDTOVal {
	
	private List<?> data = new ArrayList<>();


	public MesureDTOVal(List<?> data) {
		super();
		this.data = data;
	}

}
