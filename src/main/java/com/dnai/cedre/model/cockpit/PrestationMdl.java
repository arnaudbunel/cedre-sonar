package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class PrestationMdl implements Comparable<PrestationMdl>{
	private String datefmt;
	private String agents;
	private String duree;
	private String hdebut;
	private Long hdebuttms;
	private String hfin;
	private String poids;
	private String poidsparagent;
	private String hproximite;
	private String client;
	private String distance;
	private long idtournee;
	private String urlavp;
	private String etat;
	private boolean ecart;
	private List<PrestationDetailMdl> details = new ArrayList<>();
	
	@Override
	public int compareTo(PrestationMdl aPrestationMdl) {
		if(aPrestationMdl !=null && aPrestationMdl.getHdebuttms()>0 && this.getHdebuttms()>0) {
			return this.getHdebuttms().compareTo(aPrestationMdl.getHdebuttms());
		}
		return 1;
	}
}
