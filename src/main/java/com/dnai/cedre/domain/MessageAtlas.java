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
@Table(name = "messageatlas")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"tournee","dhsaisie"})
@lombok.ToString
public class MessageAtlas implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String message;
	
	private LocalDateTime dhsaisie;
	private LocalDateTime dhlecture;
	private LocalDateTime dhreception;
	
	private boolean lecture;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtournee")
	private Tournee tournee;
}
