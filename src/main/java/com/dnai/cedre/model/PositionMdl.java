package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class PositionMdl {
	private double latitude;
	private double longitude;
	private boolean trouve;
	private boolean multiple;
	private String adresse;
}
