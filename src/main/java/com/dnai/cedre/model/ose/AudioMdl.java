package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AudioMdl {
	
	@Attribute(required=false)
	private String url;
	
	@Attribute(required=false)
	private String dateheure;
}
