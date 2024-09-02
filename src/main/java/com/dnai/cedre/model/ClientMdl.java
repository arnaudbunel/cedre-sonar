package com.dnai.cedre.model;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ClientMdl extends ClientCommunMdl{ 

	private String tid;// identifiant tournee
	private String tour; // no tour de tournee
	private String tourorigine; // no tour de tournee origine, pour fusion

	private long debut;
	private long fin;

	private String acces;
	private String contact;
	private String recommandation;
	private String traffic;
	private List<OperationMdl> operations = new ArrayList<>();
	private List<ConsigneMdl> consignes = new ArrayList<>();
	private List<PjMdl> pieces = new ArrayList<>();
	private List<MediaMdl> sigltaudio = new ArrayList<>();
	private List<MediaMdl> sigltimage = new ArrayList<>();
	private List<MediaMdl> avpimage = new ArrayList<>();
	private String siglttext;
	private String siglttextidsignalement;
	private MediaMdl audio;
	private MediaMdl signature;
	private String presence;
	private String numavis;
	
	private ContactMdl recept;
	private List<ContactMdl> contacts = new ArrayList<>();
	
	private long dhproximite; // timestamp indiquant l'arrivée à proximité
	
	private String urlavp;
	private boolean infocovid; // pour affichage info covid dans trngeoloc
		
	public enum Presence {PRESENT,FERME,ABSENT};
	
	private boolean miseajour;
	
	private int nbchgts; // nb de changements depuis le dernier appel ose
}
