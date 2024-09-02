package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TourneeInfoCritereMdl {
	private String id; // id du tour (tournée)
	private String sid;// id du service
	private String service; // nom du service
	private double latitude;
	private double longitude;
	private String codeservice;
	private String tour;
	private String token;
}
