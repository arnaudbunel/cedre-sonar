package com.dnai.cedre.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "signalement")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"collecte","datecreation"})
@lombok.ToString
public class Signalement implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcollecte")
	private Collecte collecte;
	
	private String typesignt;
	private Date datecreation;
	private String cles3;
	private String url;
	private String texte;
	
	public enum Typesignt {
		IMAGE, AUDIO, TEXTE
	}
}
