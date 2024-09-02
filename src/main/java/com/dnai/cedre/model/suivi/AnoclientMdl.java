package com.dnai.cedre.model.suivi;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AnoclientMdl {
	private String nom;
	private List<AnomediaMdl> photos = new ArrayList<>();
	private List<AnomediaMdl> audios = new ArrayList<>();
	private String anotexte;
	private List<AnoprestationMdl> anoprestations = new ArrayList<>();
	
}
