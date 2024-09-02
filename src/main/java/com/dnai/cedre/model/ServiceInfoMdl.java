package com.dnai.cedre.model;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ServiceInfoMdl {
	private String token;
	private String service;
	private String datefmt;
	private String codeservice;
	private List<TourneeInfoMdl> tournees = new ArrayList<>();
}
