package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(strict=false)
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ReponseAuthCockpitOkMdl {

	@Attribute(required=false)
	private String mail;
	
	@Attribute(required=false)
	private String status;
}
