package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AnomalieCritereMdl {
	private String dhdebut;
	private String dhfin;
	private long idclient;
	private long idagent;
	private long idtournee;
	private int idcentre;
	private long idutilisateur;
}
