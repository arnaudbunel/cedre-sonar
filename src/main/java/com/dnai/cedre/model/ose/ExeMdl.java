package com.dnai.cedre.model.ose;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ExeMdl {
	@Attribute(required=false)
	private String start;
	@Attribute(required=false)
	private String stop;
	
	@Element(required=false)
	private ReceptMdl recept;
	
	@ElementList(entry="remonte", required=false)
	private List<RessourceMediaMdl> remontees = new ArrayList<>();
}
