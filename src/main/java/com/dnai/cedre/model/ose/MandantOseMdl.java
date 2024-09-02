package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class MandantOseMdl {
	@ElementList(entry="signalement", inline=true, required=false)
	private List<SignalementOseMdl> signalement;
	
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String nom;
}
