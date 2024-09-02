package com.dnai.cedre.model.suivi;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TourneeSuiviMdl {
	private long id;
	private String datefmt;
	private String libequipe;
	private String libtournee;
	private boolean ecart;
	private boolean signalement;	
}
