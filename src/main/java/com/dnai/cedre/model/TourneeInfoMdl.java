package com.dnai.cedre.model;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TourneeInfoMdl {
	private String identifiant;
	private long debut;
	private long fin;
	private String vehicule;
	private String tour;
	private String datefmt;
	private int nbclients;
	private List<EmportretourMdl> emportretours = new ArrayList<>();
	private List<ClientMdl> clients = new ArrayList<>();
	private String prochaineTourneeId;
	private String prochainTour;
	private String codeservice;
	private String notes;
	private boolean clos;
	private List<PositionevtMdl> positionsevt = new ArrayList<>();
	private String token;
	private long idadnai;
	private int nbagents;
	private CentreMdl centre;
	private long dhcentredepart;
	private long dhcentreretour;
	
	private boolean pesee; // pesée embarquée
	private boolean fusion; // indique fusion entre tours de la tournee
	
	private int nbchgts; // nb de changements depuis le dernier appel ose
}
