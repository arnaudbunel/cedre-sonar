package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class CMdl {
	@Attribute(required=false)
	private String nom;
	@Attribute(required=false)
	private String qte;
}
