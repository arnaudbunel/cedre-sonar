package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TempsReelMdl {
	private String agents;
	private String etat;
	private int idcentre;
	private String hdebut;
	private String hfin;
	private String libdetail;
	private int tour;
	private long idtournee;
	private boolean avecdefaut;
	private boolean fusionpossible;
	private int tourpourfusion;
	private String codeservice;

	private List<TempsReelDetailMdl> details = new ArrayList<>();
}
