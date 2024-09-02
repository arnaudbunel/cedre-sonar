package com.dnai.cedre.model.dib;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TourneedibMdl {

	private String identifiant;
	private String tour;
	private String etat;
	private long debut;
	private long fin;
	private String datefmt;
	private String vehicule;
	private List<ClientdibMdl> clients = new ArrayList<>();
	private String codeservice;
	private String token;
	private long idadnai;
	private int poidsdib;
	private long dhpesee;
}
