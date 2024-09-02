package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class GetScesResponseMdl {

	@ElementList(entry="sces", inline=true, required=false)
	private List<ScesMdl> sces;
}
