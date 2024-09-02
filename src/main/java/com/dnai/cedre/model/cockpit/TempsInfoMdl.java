package com.dnai.cedre.model.cockpit;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class TempsInfoMdl {
	private List<TempsMdl> temps = new ArrayList<>();
	private String dureeMoyenneTournee;
	private String poidsMoyen;
	private String agent;
}
