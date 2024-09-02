package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class SceTourneeMdl {
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String tour;
	
	@Attribute(required=false)
	private String date;
	
	@Attribute(required=false)
	private String daykey;
	
	@Attribute(required=false)
	private String centreID;
	
	@Attribute(required=false)
	private String pdstotal;
	
	@Element(required=false)
	private NoteMdl notes;
	
	@Element(required=false)
	private RessourceMdl ressources;
	@Element(required=false)
	private ChargementMdl chargement;
	
	@Element(required=false)
	private DessertesMdl dessertes;
}
