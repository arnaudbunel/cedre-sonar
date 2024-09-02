package com.dnai.cedre.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "historiquecodeose")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"codeservice","datetournee","tour","idose"})
@lombok.ToString
public class HistoriqueCodeOse implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String codeservice;
	
	private Date datetournee;
	
	private String tour;
	
	private String idose;
}
