package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ScesMdl {

	@ElementList(entry="sce", inline=true, required=false)
	private List<SceMdl> sce;
	
	@Attribute(required=false)
	private String da;
	@Attribute(required=false)
	private String site;
	@Attribute(required=false)
	private String nom;
}
