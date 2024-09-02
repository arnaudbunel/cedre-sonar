package com.dnai.cedre.domain;

import java.io.Serializable;

@lombok.EqualsAndHashCode(of = {"idtournee", "idagentose"})
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class TourneeAgentPk implements Serializable{

	private static final long serialVersionUID = 1L;

	private long idtournee;

	private long idagentose;
}
