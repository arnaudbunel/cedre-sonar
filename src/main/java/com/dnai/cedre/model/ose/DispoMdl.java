package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class DispoMdl {
	@Attribute(required=false)
	private String id;
	@Attribute(required=false)
	private String nom;
	@Attribute(required=false)
	private String qteprev;
	@Attribute(required=false)
	private String qtereel;
	@Attribute(required=false)
	private String qtevide;
	@Attribute(required=false)
	private String qteabsent;
	@Attribute(required=false)
	private String qtedebord;
	@Attribute(required=false)
	private String qtedeclassement;
	
	@Text(required=false)
	private String value;
	@Attribute(required=false, name="col_etage")
	private String coletage;
	@Attribute(required=false, name="col_cons")
	private String colcons;
	@Attribute(required=false)
	private String tare;
	@Attribute(required=false)
	private String volume;
	@Attribute(required=false)
	private String dechet;
	@Attribute(required=false)
	private String pdsreel;
}
