package com.dnai.cedre.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "collectedib")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"tournee","clientose"})
@lombok.ToString
public class CollecteDib implements Serializable{
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
	
	private LocalDateTime dhpassage;
	private Long distance; // mètres
	
	public enum Etat{ATTENTE,TRAITE,IMPOSSIBLE};
	
	private Integer mandant;
}
