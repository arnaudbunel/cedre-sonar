package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class DibTempsReelDetailMdl implements Comparable<DibTempsReelDetailMdl>{
	private String client;
	private String etat;
	private String dhpassage;
	private long dhpassagetms;
	
	@Override
	public int compareTo(DibTempsReelDetailMdl aTempsReelDetailMdl) {
		if(aTempsReelDetailMdl.getDhpassagetms()==0 && this.getDhpassagetms()>0) {
			return -1;
		}else if(aTempsReelDetailMdl.getDhpassagetms()>0 && this.getDhpassagetms()==0) {
			return 1;
		}else {
			if(aTempsReelDetailMdl.getDhpassagetms()>this.getDhpassagetms()) {
				return -1;
			}else {
				return 1;
			}
		}
	}
}
