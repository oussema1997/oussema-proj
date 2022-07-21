package com.sfm.obd.dto;

import com.sfm.obd.model.Entreprise;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntrepriseDto {
	private long id;
	private String nom;
	private String logo;
	private UserDto gestionnaire;
	
	public EntrepriseDto() {
	}

	public EntrepriseDto(Entreprise entreprise) {
		super();
		this.id = entreprise.getId();
		this.nom = entreprise.getNom();
		this.logo = entreprise.getLogo();
	}
}
