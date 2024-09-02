package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class SceMdl {

	@Attribute(required=false)
	private String sid;
	
	@Attribute(required=false)
	private String nom;
	
	@Element(required=false)
	private AgtsMdl agts;
	
	@Element(required=false)
	private VehiculeMdl vehicule;
}
