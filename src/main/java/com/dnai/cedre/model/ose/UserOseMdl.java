package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class UserOseMdl {
	@Attribute
	private String login;
	@Attribute
	private String pass;
}
