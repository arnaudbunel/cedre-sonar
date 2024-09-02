package com.dnai.cedre.model;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TourneeInfoLightMdl {
	private String identifiant;
	private String nom;
	private String vehicule;
	private List<String> agents = new ArrayList<>();
	private String libagents;
}
