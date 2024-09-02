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
@Table(name = "positionevt")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"tournee","dhevt"})
@lombok.ToString
public class Positionevt implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private double latitude;
	private double longitude;
	private Date dhevt;
	private String evt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtournee")
	private Tournee tournee;
	
	private int categorie;
}
