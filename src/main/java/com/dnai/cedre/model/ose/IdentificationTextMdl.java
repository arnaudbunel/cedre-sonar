package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class IdentificationTextMdl {
	@Attribute(required=false)
	private String nomprenom;
	@Attribute(required=false)
	private String qualification;
	@Attribute(required=false)
	private String telephone;
}
