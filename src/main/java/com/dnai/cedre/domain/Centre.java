package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "centre")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"nom"})
@lombok.ToString
public class Centre implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private long id;
	
	private String nom;
	private double latitude;
	private double longitude;
}
