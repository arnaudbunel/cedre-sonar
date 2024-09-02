package com.dnai.cedre.model;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class UserInfoMdl {
	//private String nom;
	//private String prenom;
	private String token;
	
	private String identifiant;
	private boolean connecte;
	
	private List<TourneeInfoLightMdl> tournees = new ArrayList<>();
}
