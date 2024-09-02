package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AMdl {
	@Attribute(required=false)
	private String nom;
	
	@Text(required=false)
	private String value;
}
