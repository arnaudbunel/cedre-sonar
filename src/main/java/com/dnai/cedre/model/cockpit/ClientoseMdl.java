package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.AllArgsConstructor
public class ClientoseMdl implements Comparable<ClientoseMdl>{
	private long id;
	private String nom;
	
	@Override
	public int compareTo(ClientoseMdl arg0) {
		return this.nom.compareTo(arg0.getNom());
	}
}
