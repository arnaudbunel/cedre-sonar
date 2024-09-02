package com.dnai.cedre.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "eventtechnique")
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class EventTechnique implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private LocalDateTime dhevent;
	private long idadnai;
	private String codeservice;
	private String evt;
	private String dataevt;
	private String token;
}
