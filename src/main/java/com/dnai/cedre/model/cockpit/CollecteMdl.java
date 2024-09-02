package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class CollecteMdl {
	private long idcollecte;
	private String client;
	private String idclientose;
	private String etat;
	private String hdebut;
	private String hfin;
	private String urlavp;
	private List<PrestationEncoursMdl> prestations = new ArrayList<>();
}
