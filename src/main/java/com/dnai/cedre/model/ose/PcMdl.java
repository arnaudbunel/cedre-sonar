package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class PcMdl {
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String nom;
	
	@Attribute(required=false)
	private String mandant;
	
	@Attribute(required=false)
	private String status;
	
	@Element(required=false)
	private ExeMdl exe;
	
	@Element(required=false)
	private LocMdl loc;
	
	@Element(required=false)
	private ConsignesMdl consignes;
	
	@Element(required=false)
	private OperationsMdl operations;
	
	// Ajouts nouvelles données
	@ElementList(entry="doc", required=false)
	private List<DocMdl> docs;
	
	@ElementList(entry="ct", required=false)
	private List<CtMdl> ctc;
		
	public enum Status {ATT,ENCOURS,CLOS,ABSENT,FERME};
}
