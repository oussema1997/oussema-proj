package com.sfm.obd.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MesureBrut {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private Date date;

	@Column
	private String mesure;

	public MesureBrut(Date date, String mesure) {
		super();
		this.date = date;
		this.mesure = mesure;
	}

	@Override
	public String toString() {
		return "MesureBrut [id=" + id + ", date=" + date + ", mesure=" + mesure + "]";
	}

}
