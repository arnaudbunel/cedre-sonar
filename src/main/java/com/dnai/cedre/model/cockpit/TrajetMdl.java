package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TrajetMdl implements Comparable<TrajetMdl>{
	private String datefmt;
	private String agents;
	private String duree;
	private String hdebut;
	private Long hdebuttms;
	private String hfin;
	private String distance;
	private String libdetail;
	private String poids;
	private String poidsparagent;
	private String hdepartcentre;
	private String hretourcentre;
	
	private List<TrajetClientMdl> trajetclients = new ArrayList<>();
	
	private CentreTourneeMdl centretournee;
	
	@Override
	public int compareTo(TrajetMdl aTrajetMdl) {
		if(aTrajetMdl.getHdebuttms()>0 && this.getHdebuttms()>0) {
			return this.getHdebuttms().compareTo(aTrajetMdl.getHdebuttms());
		}
		return 1;
	}
}
