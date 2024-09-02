package com.dnai.cedre.model;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ClientCommunMdl {
	protected int numero;
	protected String id;
	protected String identifiant;
	protected String etat; // TRAITE ou ATTENTE
	protected String nom;
	protected String adr1;
	protected String adr2;
	protected String codepostal;
	protected String ville;
	protected double latitude;
	protected double longitude;
	protected boolean geoloc;
	
	protected long tmsmaj; // indicateur de sync client - server
	protected long idtourneeadnai;
	protected long idcollecteadnai;
	protected int nbagents;
	protected String horaires;
	protected String mandant;
	
	protected List<SignalementMdl> signalements = new ArrayList<>();
	
	public enum Etat{ATTENTE, TRAITE}
}
