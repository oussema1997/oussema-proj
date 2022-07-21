package com.sfm.obd.dto.mesures;

import java.util.Date;

import com.sfm.obd.enumer.Criticite;

import com.sfm.obd.enumer.TypeAlarme;
import com.sfm.obd.model.Boitier;
import com.sfm.obd.model.Mesure;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Alarme {

	private long id;
	private Date date;
	private String libelle;
	private String phase;
	private TypeAlarme typeAlarme;
	private Criticite criticite;
	private Mesure mesure;
	private Boitier boitier;
	private Boolean resolu;

	public Alarme(long id, Date date,String libelle, String phase, TypeAlarme typeAlarme, Criticite criticite, Mesure mesure, Boitier boitier, Boolean resolu) {
		super();
		this.id = id;
		this.libelle = libelle;
		this.phase = phase;
		this.typeAlarme = typeAlarme;
		this.date = date;
		this.criticite = criticite;
		this.mesure = mesure;
		this.boitier = boitier;
		this.resolu = resolu;
	}



}
