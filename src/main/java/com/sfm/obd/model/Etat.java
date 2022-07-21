package com.sfm.obd.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Etat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private Date date;

	@ManyToOne
	@JoinColumn(name = "etat")
	private TypeEtat etat;
	
	@Column
	private Boolean resolu;

	@Column
	private String phase;

	@ManyToOne
	@JoinColumn(name = "boitier")
	private Boitier boitier;

	@ManyToOne
	@JoinColumn(name = "mesure")
	private Mesure mesure;

	public Etat(Date date, TypeEtat etat, Boitier boitier, Boolean resolu, String phase, Mesure mesure) {
		super();
		this.date = date;
		this.etat = etat;
		this.boitier = boitier;
		this.resolu = resolu;
		this.phase = phase;
		this.mesure = mesure;
	}

}
