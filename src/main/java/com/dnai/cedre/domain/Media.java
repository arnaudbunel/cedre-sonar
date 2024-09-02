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
@Table(name = "media")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"cles3"})
@lombok.ToString
public class Media implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String idtournee; // id ose // TODO à supprimer
	private String idclient; // id ose // TODO à supprimer
	private String typemedia;
	private Date datecreation;
	private String cles3;
	private String url;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcollecte")
	private Collecte collecte;
	
	public enum Typemedia {
		SIGNALEMENTIMAGE, SIGNALEMENTAUDIO, SIGNATURE, INFOCLIENT, AVPIMAGE
	}
}
