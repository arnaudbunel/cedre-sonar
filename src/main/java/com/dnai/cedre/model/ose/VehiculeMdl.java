package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class VehiculeMdl {
	@Attribute
	private String id;
	@Attribute
	private String nom;
}
