package com.sfm.obd.dto;

import com.sfm.obd.model.Utilisateur;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthToken {

	private String token;
	private Utilisateur user;

	public AuthToken() {
	}

	public AuthToken(String token, Utilisateur user) {
		this.token = token;
		this.user = user;
	}
}
