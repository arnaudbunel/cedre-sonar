package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class DessertesMdl {
	@Attribute(required=false)
	private String start;
	@Attribute(required=false)
	private String stop;
	
	@ElementList(entry="pc", inline=true, required=false)
	private List<PcMdl> pc;

}
