package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vehicule")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"immatriculation"})
@lombok.ToString
public class Vehicule implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String immatriculation;
	
	private boolean pesee;
}
