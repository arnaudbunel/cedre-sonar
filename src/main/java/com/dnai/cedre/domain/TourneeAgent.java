package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity 
@IdClass(TourneeAgentPk.class)
@Table(name = "tournee_agent")
@lombok.EqualsAndHashCode(of = {"idtournee", "idagentose"})
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class TourneeAgent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "idtournee")
	@Id
	private long idtournee;
	
	@Column(name = "idagentose")
	@Id
	private long idagentose;

}
