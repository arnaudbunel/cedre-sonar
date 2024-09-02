package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class PrestationEncoursMdl {
	private long idprestation;
	private long idcollecte;
	private String libelle;
	private int qteprevue;
	private int qtereel;
	private int qtevide;
	private int qteabsent;
	private double poids;
	private int nbagents;
	private String operation;
	private String dispositif;
}
