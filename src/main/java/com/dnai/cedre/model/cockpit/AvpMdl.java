package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AvpMdl {
	@Element(required=false)
	private String idavp;
	@Element(required=false)
	private String nomClient;
	@Element(required=false)
	private String adresse;
	@Element(required=false)
	private String codepostal;
	@Element(required=false)
	private String ville;
	@Element(required=false)
	private String dateAvp;
	@Element(required=false)
	private String heureAvp;
	@Element(required=false)
	private String agents;
	@Element(required=false)
	private String vehicule;
	@Element(required=false)
	private String infosSignataire;
	@Element(required=false)
	private String signature;
	@Element(required=false)
	private boolean signaturePresente;
	@Element(required=false)
	private boolean photosPresentes;
	
	private String idtourneeose;
	private String idclientose;
	
	@ElementList(entry="details", inline=true, required=false)
	private List<AvpDetailMdl> details = new ArrayList<>();
	
	@ElementList(entry="photos", inline=true, required=false)
	private List<AvpPhotoMdl> photos = new ArrayList<>();
}
