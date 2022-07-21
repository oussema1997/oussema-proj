package com.sfm.obd.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sfm.obd.enumer.UserRoles;
import com.sfm.obd.model.Entreprise;
import com.sfm.obd.model.Utilisateur;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

	private long id;
	private String password;
	@JsonIgnore
	private UserRoles role;
	private boolean enable;
	private String email;
	private String phone;
	private String firstname;
	private String lastname;
	private Entreprise entreprise;
	
	public UserDto() {
	}

	public UserDto(Utilisateur utilisateur) {
		super();
		this.id = utilisateur.getId();
		this.enable = utilisateur.isEnable();
		this.email = utilisateur.getEmail();
		this.phone = utilisateur.getPhone();
		this.firstname = utilisateur.getFirstname();
		this.lastname = utilisateur.getLastname();
		this.entreprise = utilisateur.getEntreprise();
	}

}
