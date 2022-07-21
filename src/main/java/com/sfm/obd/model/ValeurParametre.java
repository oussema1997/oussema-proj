package com.sfm.obd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "parametre", "boitier" }))
public class ValeurParametre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private String valeur;
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "boitier")
	private Boitier boitier;
	@ManyToOne
	@JoinColumn(name = "parametre")
	private Parametre parametre;

	public ValeurParametre(String valeur, Boitier boitier, Parametre parametre) {
		super();
		this.valeur = valeur;
		this.boitier = boitier;
		this.parametre = parametre;
	}

	@Override
	public String toString() {
		return parametre.getAttribut() + " : " + valeur;
	}

}
