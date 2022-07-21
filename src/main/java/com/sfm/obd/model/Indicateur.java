package com.sfm.obd.model;

import javax.persistence.*;

import com.sfm.obd.enumer.TypeBoitier;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Indicateur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true)
	private String libelle;

	@Column()
	private String unite;

	//@Enumerated(EnumType.STRING)
	//@Column
	//private TypeBoitier typeBoitier;
	
	@Override
	public String toString() {
		return libelle;
	}

}
