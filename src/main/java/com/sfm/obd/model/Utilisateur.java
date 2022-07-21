package com.sfm.obd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sfm.obd.dto.UserDto;
import com.sfm.obd.enumer.UserRoles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Utilisateur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true)
	private String email;
	@Column
	@JsonIgnore
	private String password;
	@Column
	private String phone;
	@Column
	private String firstname;
	@Column
	private String lastname;
	@Enumerated(EnumType.STRING)
	@Column
	private UserRoles role;
	@Column
	private boolean enable;
	
	@ManyToOne
	@JoinColumn(name = "entreprise")
	private Entreprise entreprise;

	@JsonIgnoreProperties({"gestionnaire"})
	@ManyToOne
	@JoinColumn(name = "entrepriseCliente")
	private EntrepriseCliente entrepriseCliente;
	
	public Utilisateur() {
	}

	public Utilisateur(UserDto userDto) {
		super();
		this.id = userDto.getId();
		this.password = userDto.getPassword();
		this.enable = userDto.isEnable();
		this.email = userDto.getEmail();
		this.phone = userDto.getPhone();
		this.firstname = userDto.getFirstname();
		this.lastname = userDto.getLastname();
		this.entreprise = userDto.getEntreprise();
	}
	
	@Override
	public String toString() {
		return firstname + " "+ lastname + " ["
				+ "actif=" + ((enable)  ? "oui" : "non")
				+ ", email=" + email
				+ ", téléphone=" + phone
				+ ", entreprise=" + ( entreprise != null ? entreprise.getNom() : null )
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utilisateur other = (Utilisateur) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
