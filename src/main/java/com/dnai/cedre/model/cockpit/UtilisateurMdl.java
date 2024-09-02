package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class UtilisateurMdl {
	
	private long id;
	private String email;
	private String mandant;
	private boolean manager;
	private int idmandant;
}
