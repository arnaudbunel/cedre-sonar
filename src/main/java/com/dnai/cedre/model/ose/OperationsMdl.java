package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class OperationsMdl {
	@ElementList(entry="op", inline=true, required=false)
	private List<OpMdl> op;
}
