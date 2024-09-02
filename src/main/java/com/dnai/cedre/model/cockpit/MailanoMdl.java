package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class MailanoMdl {
	private String destinataires;
	private String nomclient;
	private String libequipe;
	private String datefmt;
	private List<String> urlsphoto = new ArrayList<>();	
}
