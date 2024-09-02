package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TempsMdl implements Comparable<TempsMdl>{
	private String datefmt;
	private String duree;
	private String hdebut;
	private Long hdebuttms;
	private String hfin;
	private String distance;
	private String libdetail;
	private String poids;
	private String agent;
	private String hdepartcentre;
	private String hretourcentre;
	
	private List<TempsClientMdl> tempsclients = new ArrayList<>();
	
	private CentreTourneeMdl centretournee;
	
	@Override
	public int compareTo(TempsMdl aTempsMdl) {
		if(aTempsMdl.getHdebuttms()>0 && this.getHdebuttms()>0) {
			return this.getHdebuttms().compareTo(aTempsMdl.getHdebuttms());
		}
		return 1;
	}
}
