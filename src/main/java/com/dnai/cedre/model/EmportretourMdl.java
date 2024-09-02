package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class EmportretourMdl implements Comparable<EmportretourMdl>{
	private String libelle;
	private String dechet;
	private String alivrer;
	private String arecuperer;
	private boolean important;
	
	@Override
	public int compareTo(EmportretourMdl arg0) {
		return arg0.alivrer.compareTo(this.alivrer);
	}
}
