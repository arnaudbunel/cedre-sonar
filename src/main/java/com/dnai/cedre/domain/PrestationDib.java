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
@Table(name = "prestationdib")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"libelle","collectedib"})
@lombok.ToString
public class PrestationDib implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String libelle;
	
	private int qteprevu;
	private int qtereel;
	private int qteabsent;
	private int qtevide;
	private int qtedebord;
	private int qtedeclassement;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcollectedib")
	private CollecteDib collectedib;
	
	private int position;
	
	private String operation;
	private String dispositif;
	
	private String opid;
	private String dispoid;
	private String compo;
	private String cleose;
}
