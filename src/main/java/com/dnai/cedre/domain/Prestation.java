package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "prestation")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"libelle","collecte"})
@lombok.ToString
public class Prestation implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String libelle;
	
	private String qteprevu;
	private String qtereel;
	private int qteabsent;
	private int qtevide;
	private double poids;
	private double poidsparagent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcollecte")
	private Collecte collecte;
	
	private int position;
	
	private String operation;
	private String dispositif;
	
	private String opid;
	private String dispoid;
	private String cleose;
}
