package com.sfm.obd.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.sfm.obd.enumer.Criticite;

import com.sfm.obd.enumer.TypeAlarme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TypeEtat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private String libelle;
	@Enumerated(EnumType.STRING)
	@Column
	private Criticite criticite;
	@Enumerated(EnumType.STRING)
	@Column
	private TypeAlarme typeAlarme;
	@Column(length = 4000)
	private String description;
	@Column
	private boolean alarme;
	
	@Override
	public String toString() {
		return libelle + " ["
				+ ", criticité=" + criticite
				+ ", description=" + description
				+ ", alarme=" + alarme
				+ "]";
	}

}
