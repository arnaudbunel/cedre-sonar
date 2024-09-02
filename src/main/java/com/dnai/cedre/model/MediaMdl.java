package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString(exclude = {"base64media"})
public class MediaMdl {
	private String base64media;
	private String idtournee;
	private String idclient;
	private String typemedia;
	private String tour;
	private String etatsync; // CLIENT ou SERVER
	private String fichier;
	private String url;
	private long dateheure; // timestamp
	private long idcollecte;
	private long idprestation;
	private long id;
	private String motif;
	private double latitude;
	private double longitude;
	private String idsignalementose;

	public enum Typemedia {
		SIGNALEMENTIMAGE, SIGNALEMENTAUDIO, SIGNATURE, INFOCLIENT
	}
	
	public enum Etatsync {
		CLIENT, SERVER
	}
}
