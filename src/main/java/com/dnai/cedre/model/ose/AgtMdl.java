package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AgtMdl {
	@Attribute(required=false)
	private String rang;
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String nom;
}
