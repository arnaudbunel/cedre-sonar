package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AuthentificationRetourMdl {
	private boolean authentifie;
	private String utilisateur;
	private String message;
	private String role;
	private long idutilisateur;
	private int idmandant;
}
