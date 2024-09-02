package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OperationMdl {
	private String id;
	private String type;
	private String libelle;
	private String nom;
	private String qteprev;
	private String qtereel;
	private double pdsreel;
	private String consigne;
	private String dispoid;
	private int bacvide;
	private int bacabsent;
	private String dechet;
	private boolean depotfourniture;
	private String codetype;
	private double tare;
	private double pdspeseeembarquee;
	private long idprestationadnai;
	private String cleose;
}
