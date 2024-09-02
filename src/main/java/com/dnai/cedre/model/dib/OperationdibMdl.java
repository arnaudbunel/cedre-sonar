package com.dnai.cedre.model.dib;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OperationdibMdl {
	private String id;
	private String type;
	private String libelle;
	private String nom;
	private String dispoid;
	private String compo;
	private int qteprev;
	private int qtereel;
	private int qtevide;
	private int qteabsent;
	private int qtedebord;
	private int qtedeclassement;
	private String dechet;
	private String codetype;
	private long idprestationadnai;
	private String cleose;
}
