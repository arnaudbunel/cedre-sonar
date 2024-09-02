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
@Table(name = "collecte")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"tournee","clientose"})
@lombok.ToString
public class Collecte implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String etat; // FERME, ABSENT, TRAITE, IMPOSSIBLE
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtournee")
	private Tournee tournee;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idclientose")
	private Clientose clientose;
	
	private Date dhdebut;
	private Date dhfin;
	private Date dhproximite;
	private Long distance; // mètres
	private String urlavp; // url avis de passage
	private String receptitre;
	private String recepfonction;
	
	private Integer mandant;
	
	private int notourorigine;
}
