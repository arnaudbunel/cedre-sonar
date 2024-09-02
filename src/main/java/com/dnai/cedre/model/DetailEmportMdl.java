package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class DetailEmportMdl {
	private String libelle;
	private String dechet;
	private String prestation;
	//private String quantite;
	
	private int qteALivrer;
	private int qteARecuperer;
}
