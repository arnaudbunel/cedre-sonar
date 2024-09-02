package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class DibTempsReelMdl {
	private String agents;
	private String etat;
	private int idcentre;
	private String dhdebut;
	private String dhfin;
	private String dhpesee;
	private String libdetail;
	private String poidsfmt;
	private long idtournee;
	private String codeservice;

	private List<DibTempsReelDetailMdl> details = new ArrayList<>();
}
