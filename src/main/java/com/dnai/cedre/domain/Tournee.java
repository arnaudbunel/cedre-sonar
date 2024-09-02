package com.dnai.cedre.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tournee")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"datetournee","idose","notour"})
@lombok.ToString
public class Tournee implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private Date datetournee;
	
	private String idose;
	
	private int notour;
	private int nombretour;
	
	private String vehicule;
	
	// denormalisation
	private String libequipe;
	private boolean signt;
	private boolean ecart;
	
	private Date dhdebut;
	private Date dhfin;
	
	private Long distance; // mètres
	
	private int idcentre;
	
	private String etat; // DEBUT, ENCOURS, FIN 
	private Date dhcentredepart;
	private Date dhcentreretour;
	
	private String codeservice;
	
	private String token;
	
	private String typetournee;
	private Integer poidsdib;
	private LocalDateTime dhpesee;
	
	private String cleflux;
	private LocalDateTime dhmajose;
	
	private boolean fusion;
	
	public enum Typetournee {
		DIB, COLLECTE
	}
	
	public enum Etat {
		DEBUT, ENCOURS, FIN, PESEE, VIDAGE
	}
}
