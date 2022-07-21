package com.sfm.obd.dto.mesures;

import com.sfm.obd.enumer.Criticite;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AlarmeParBoitier {
	
	private long id;
	private Date date;
	private String libelle;
	private Criticite criticite;
	private Boolean resolu;
	private String refBoitier;
	private String nomSite;
	

	public AlarmeParBoitier(long id, Date date, String libelle, Criticite criticite, Boolean resolu,
                            String refBoitier, String nomSite) {
		super();
		this.id = id;
		this.libelle = libelle;
		this.date = date;
		this.criticite = criticite;
		this.resolu = resolu;
		this.refBoitier = refBoitier;
		this.nomSite = nomSite;
	}

}
