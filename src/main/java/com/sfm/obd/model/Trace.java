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
import lombok.Setter;

@Getter
@Setter
@Entity
public class Trace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private Date date;

	@Column(length = 2000)
	private String action;

	@ManyToOne
	@JoinColumn(name = "user")
	private Utilisateur user;

	@ManyToOne
	@JoinColumn(name = "etat")
	private Etat etat;

	public Trace() {
		super();
	}

	public Trace(Date date, String action, Utilisateur user) {
		super();
		this.date = date;
		this.action = action;
		this.user = user;
	}
	public Trace(Date date, String action, Utilisateur user, Etat etat) {
		super();
		this.date = date;
		this.action = action;
		this.user = user;
		this.etat = etat;
	}

}
