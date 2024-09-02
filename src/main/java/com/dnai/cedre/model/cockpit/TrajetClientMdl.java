package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TrajetClientMdl {
	private String duree;
	private String hproximite;
	private String hdebut;
	private String hfin;
	private String distance;
	private InfosClientMdl infosclient;
	private String poids;
	private String poidsparagent;
	private String dureeattente;
	private String etat;
}
