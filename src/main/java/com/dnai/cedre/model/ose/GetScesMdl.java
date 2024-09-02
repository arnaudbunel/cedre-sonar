package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict=false)
@lombok.Getter
@lombok.Setter
@lombok.ToString
public class GetScesMdl {
	@Element(name="user")
	private UserOseMdl userOse;
}
