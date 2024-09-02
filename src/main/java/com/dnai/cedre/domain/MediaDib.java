package com.dnai.cedre.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mediadib")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"cles3"})
@lombok.ToString
public class MediaDib implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String typemedia;
	private LocalDateTime dhcreation;
	private String cles3;
	private String url;
	private String motif;
	private double latitude;
	private double longitude;
	
	private long idcollectedib; // dénormalisation pour perf
	
	private long idprestationdib; // dénormalisation pour perf
	
	public enum Typemedia {
		SIGNALEMENTIMAGE
	}
}
