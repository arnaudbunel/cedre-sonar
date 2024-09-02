package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AdresseMdl {
	private String adr1;
	private String adr2;
	private String codepostal;
	private String ville;
	private String libadr;
	//private String hashadr;
	private double latitude;
	private double longitude;
}
