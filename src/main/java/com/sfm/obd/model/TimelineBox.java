package com.sfm.obd.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.sfm.obd.enumer.PairingBox;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TimelineBox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private Date date;

	@Enumerated(EnumType.STRING)
	@Column
	private PairingBox action;

	@ManyToOne
	@JoinColumn(name = "user")
	private Utilisateur user;


	@ManyToOne
	@JoinColumn(name = "voiture")
	private Voiture voiture;

	@ManyToOne
	@JoinColumn(name = "boitier")
	private Boitier boitier;

	public TimelineBox(Date date, PairingBox action, Utilisateur user, Boitier boitier) {
		super();
		this.date = date;
		this.action = action;
		this.user = user;
		this.boitier = boitier;
	}

	public TimelineBox(Date date, PairingBox action, Utilisateur user, Voiture voiture, Boitier boitier) {
		super();
		this.date = date;
		this.action = action;
		this.user = user;
		this.voiture = voiture;
		this.boitier = boitier;
	}

}
