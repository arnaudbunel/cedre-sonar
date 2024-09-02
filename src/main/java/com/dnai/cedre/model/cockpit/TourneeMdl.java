package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TourneeMdl {
	private String agents;
	private String hdebut;
	private String vehicule;
	private String libdetail;
	private String codeservice;
	private int tour;
	private long idtournee;
	
	private List<CollecteMdl> collectes = new ArrayList<>();
}
