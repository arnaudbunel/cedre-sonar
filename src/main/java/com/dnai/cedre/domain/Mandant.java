package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mandant")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"libelle"})
@lombok.ToString
public class Mandant implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String libelle;
}
