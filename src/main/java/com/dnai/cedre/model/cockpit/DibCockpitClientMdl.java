package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class DibCockpitClientMdl implements Comparable<DibCockpitClientMdl>{

	private String datefmt;
	private String agents;
	private String hpassage;
	private Long hpassagetms;
	private String client;
	private String distance;
	private long idtournee;
	private String etat;
	private List<DibCockpitClientDetailMdl> details = new ArrayList<>();
	
	private List<DibPhotoMdl> photos = new ArrayList<>();
	
	@Override
	public int compareTo(DibCockpitClientMdl dibCockpitClientMdl) {
		if(dibCockpitClientMdl !=null && dibCockpitClientMdl.getHpassagetms()>0 && this.getHpassagetms()>0) {
			return this.getHpassagetms().compareTo(dibCockpitClientMdl.getHpassagetms());
		}
		return 1;
	}

}
