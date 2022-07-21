package com.sfm.obd.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestMesure {

	private long idBoitier;
	private long idIndicateur;
	private Date dateDebut;
	private Date dateFin;
	private boolean resolu;
	
}
