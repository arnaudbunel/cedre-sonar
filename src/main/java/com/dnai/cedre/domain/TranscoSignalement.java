package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transcosignalement")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"idcedre","idmandant"})
@lombok.ToString
public class TranscoSignalement implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String idcedre;
	private String titre;
	private String description;
	private int idmandant;
	private String motifadnai;
	private boolean actif;
	private String imageurl;
}
