package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Element;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ConsignesMdl {
	@Element(required=false)
	private String horaires;
	@Element(required=false)
	private String adresseacces;
	@Element(required=false)
	private String contact;
	@Element(required=false)
	private String recommandations;
}
