package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity 
@IdClass(UtilisateurGroupesitePk.class)
@Table(name = "utilisateur_groupesite")
@lombok.EqualsAndHashCode(of = {"idutilisateur","idgroupesite"})
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class UtilisateurGroupesite implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "idgroupesite")
	@Id
	private long idgroupesite;
	
	@Column(name = "idutilisateur")
	@Id
	private long idutilisateur;
}
