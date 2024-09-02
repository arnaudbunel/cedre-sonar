package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ReceptMdl {
	
	@Attribute(required=false)
	private String id;
	
	@Attribute(required=false)
	private String nomprenom;
	
	@Attribute(required=false)
	private String fonction;
	
	@Attribute(required=false)
	private String tel;
	
	@Attribute(required=false)
	private String audio;
	
	@Attribute(required=false)
	private String signature;

}
