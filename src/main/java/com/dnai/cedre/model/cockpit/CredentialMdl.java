package com.dnai.cedre.model.cockpit;

@lombok.Getter
@lombok.Setter
@lombok.ToString (exclude = {"password"})
public class CredentialMdl {

	private String login;
	private String password;
	
}
