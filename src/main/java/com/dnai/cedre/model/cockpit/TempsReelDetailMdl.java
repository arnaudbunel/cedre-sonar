package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TempsReelDetailMdl implements Comparable<TempsReelDetailMdl>{
	private String client;
	private String etat;
	private String hproximite;
	private String hdebut;
	private String hfin;
	private long hdebuttms;
	
	@Override
	public int compareTo(TempsReelDetailMdl aTempsReelDetailMdl) {
		if(aTempsReelDetailMdl.getHdebuttms()==0 && this.getHdebuttms()>0) {
			return -1;
		}else if(aTempsReelDetailMdl.getHdebuttms()>0 && this.getHdebuttms()==0) {
			return 1;
		}else {
			if(aTempsReelDetailMdl.getHdebuttms()>this.getHdebuttms()) {
				return -1;
			}else {
				return 1;
			}
		}
	}
}
