package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TempsClientMdl {
	private String duree;
	private String hproximite;
	private String hdebut;
	private String hfin;
	private String poids;
	private String tempstrajet;
	private InfosClientMdl infosclient;
	private String etat;
}
