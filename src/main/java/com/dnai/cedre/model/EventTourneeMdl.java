package com.dnai.cedre.model;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class EventTourneeMdl {
	private long idevent;
	private long idtournee;
	private long idmessage;
	private String eventtype; // FUSION, MESSAGE
	private String dhfmtcreation;
	private String message;
}
