package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Element;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class RessourceMdl {
	@Element(required=false)
	private AgtsMdl agts;
	@Element(required=false)
	private VehiculeMdl vehicule;


	
}
