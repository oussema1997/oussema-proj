package com.sfm.obd.model;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.enumer.TypeBoitier;
import com.sfm.obd.utils.Formatter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Boitier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private String imei;
	@Column
	private String reference;
	@Column
	private Date dateInstallation; // Date d'installation sur le dernier site
	@Column
	private Date derniereConnection; // Date de dernière connection à la plateforme
	@Column
	private Date softLastUpdate; // Date de dernière mise à jour de son logiciel
	@Column
	private String versionSoft; // Date de dernière mise à jour de son logiciel
	@Column
	private boolean enable;

	@Enumerated(EnumType.STRING)
	@Column
	private TypeBoitier typeBoitier;


	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "voiture")
	private Voiture voiture;

	@ManyToOne
	@JoinColumn(name = "entreprise")
	private Entreprise entreprise;

	@JsonIgnore
	@OneToMany(mappedBy = "boitier",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL)
	private List<Mesure> mesures;
	
	@Enumerated(EnumType.STRING)
	@Column
	private PairingBox etat;
	public Boitier(String imei, String reference, Date derniereConnection, boolean enable, PairingBox etat, String versionSoft) {
		super();
		this.imei = imei;
		this.reference = reference;
		this.derniereConnection = derniereConnection;
		this.enable = enable;
		this.etat = etat;
		this.versionSoft = versionSoft;

	}
	public Boitier(String imei, String reference, Date derniereConnection, boolean enable, PairingBox etat, String versionSoft, List<Mesure> mesures) {
		super();
		this.imei = imei;
		this.reference = reference;
		this.derniereConnection = derniereConnection;
		this.enable = enable;
		this.etat = etat;
		this.versionSoft = versionSoft;
		this.mesures = mesures;
	}
	
	@Override
	public String toString() {
		return reference + " ["
				+ "actif=" + ((enable)  ? "oui" : "non")
				+ ", imei=" + imei
				+ ", installation=" + ( dateInstallation != null ? Formatter.formatDate(dateInstallation) : null )
				+ ", Dernière connexion=" + ( derniereConnection != null ? Formatter.formatDate(derniereConnection) : null )
				+ ", Mise à jour logiciel=" + ( softLastUpdate != null ? Formatter.formatDate(softLastUpdate) : null )
				+ ", entreprise=" + ( entreprise != null ? entreprise.getNom() : null )
				+ "]";
	}

}
