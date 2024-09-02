package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clientose")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"idose","nom"})
@lombok.ToString
public class Clientose implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String idose;
	
	private String nom;
	private String adr1;
	private String adr2;
	private String codepostal;
	private String ville;
	private double latitude;
	private double longitude;
	
	private boolean verifie;
	
	private String srcgeoloc;
	
	public enum Srcgeoloc{ADNAI,OSE};
}
