package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Element;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ChargementMdl {
	@Element(required=false)
	private EmportMdl emport;
	
	@Element(required=false)
	private RetourMdl retour;
}
