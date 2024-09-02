package com.dnai.cedre.domain;

import java.io.Serializable;

@lombok.EqualsAndHashCode(of = {"idgroupesite", "idutilisateur"})
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class UtilisateurGroupesitePk implements Serializable{

	private static final long serialVersionUID = 1L;

	private long idgroupesite;

	private long idutilisateur;
}
