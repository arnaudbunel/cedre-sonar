package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class SignalementOseMdl {
	
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String titre;
	@Attribute(required=false)
	private String illus;
}
