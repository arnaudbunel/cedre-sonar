package com.dnai.cedre.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "eventtournee")
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class EventTournee implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long idtournee;
	
	private String event;
	private LocalDateTime dhsaisie;
	private LocalDateTime dhreception;
	private LocalDateTime dhaction;
		
	private String dataevent;
}
