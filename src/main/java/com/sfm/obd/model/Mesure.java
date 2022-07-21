package com.sfm.obd.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Mesure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private Date date;
	@Column
	private long datems;
	@Column
	private String val1;
	@Column
	private String val2;
	@Column
	private String val3;
	@Column
	private String val4;
	@Column
	private String val5;
	@Column
	private String erreur;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "boitier")
	private Boitier boitier;

	@ManyToOne
	@JoinColumn(name = "indicateur")
	private Indicateur indicateur;

	@OneToMany(mappedBy = "mesure")
	private List<Erreur> erreurs;

	public Mesure(Date date, String val1, String val2, String val3, String val4, String val5, Boitier boitier, Indicateur indicateur) {
		super();
		this.date = date;
		this.val1 = val1;
		this.val2 = val2;
		this.val3 = val3;
		this.val4 = val4;
		this.val5 = val5;
		this.boitier = boitier;
		this.indicateur = indicateur;
	}

	@Override
	public String toString() {
		return "Mesure [id=" + id + ", date=" + date + ", val1=" + val1 + ", val2=" + val2 + ", val3=" + val3
				+ ", boitier=" + boitier + ", indicateur=" + indicateur + "]";
	}

}
