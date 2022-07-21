package com.sfm.obd.dto;

import java.util.List;

import com.sfm.obd.model.Utilisateur;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoitureAllUsers {

	private List<Utilisateur> followers;
	private List<Utilisateur> unFollowers;

	public VoitureAllUsers(List<Utilisateur> followers, List<Utilisateur> unFollowers) {
		super();
		this.followers = followers;
		this.unFollowers = unFollowers;
	}

}
