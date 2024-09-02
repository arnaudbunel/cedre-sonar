package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class PositionevtMdl {
	private double latitude;
	private double longitude;
	private long dhevt;
	private String evt; // DEBUT_TOURNEE, DEBUT_CLIENT:<idose>, FIN_CLIENT:<idose>, FIN_TOURNEE
	private int categorie; /* 1 : position, 2 : evt réseau, 3 : action utilisateur */
}
