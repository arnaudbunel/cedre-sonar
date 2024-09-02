package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OpMdl {
	@Attribute(required=false)
	private String compo;
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String type;
	@Attribute(required=false)
	private String nom;
	@Attribute(required=false)
	private String prestation;
	
	//@Element(required=false)
	//private DispoMdl dispo;
	
	@ElementList(entry="dispo", inline=true, required=false)
	private List<DispoMdl> dispo;
	
	@Attribute(required=false)
	private String pdscompo;
}
