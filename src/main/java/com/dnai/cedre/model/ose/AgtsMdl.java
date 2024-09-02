package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class AgtsMdl {
	@ElementList(entry="agt", inline=true, required=false)
	private List<AgtMdl> agt;
}
