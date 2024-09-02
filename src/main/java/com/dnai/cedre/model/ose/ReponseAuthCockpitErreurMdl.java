package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(strict=false)
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ReponseAuthCockpitErreurMdl {

	@Attribute(required=false)
	private String type;
	
	@Text(required=false)
	private String value;
}
