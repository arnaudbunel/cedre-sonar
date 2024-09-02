package com.dnai.cedre.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "utilisateur")
@lombok.Getter
@lombok.Setter
@lombok.EqualsAndHashCode(of = {"email"})
@lombok.ToString(exclude={"password"})
public class Utilisateur implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String email;
	private String password;
	private String role;
	
	private int mandant;

	public enum ROLE {
		ROLE_ADMIN, ROLE_CEDRE, ROLE_CLIENT, ROLE_CEDRE_MANAGER
	}
}
