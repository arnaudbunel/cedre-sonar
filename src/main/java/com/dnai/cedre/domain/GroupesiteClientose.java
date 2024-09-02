package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity 
@IdClass(GroupesiteClientosePk.class)
@Table(name = "groupesite_clientose")
@lombok.EqualsAndHashCode(of = {"idgroupesite", "idclientose"})
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class GroupesiteClientose implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "idgroupesite")
	@Id
	private long idgroupesite;
	
	@Column(name = "idclientose")
	@Id
	private long idclientose;
}
