package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class SignalementOseRootMdl {
	@ElementList(entry="mandant", inline=true, required=false)
	private List<MandantOseMdl> mandant;
}
