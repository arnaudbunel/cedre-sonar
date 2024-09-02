package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class LocMdl {
	@Attribute(required=false)
	private String lat;
	@Attribute(required=false)
	private String lng;
	
	@ElementList(entry="a", inline=true, required=false)
	private List<AMdl> a;
}
