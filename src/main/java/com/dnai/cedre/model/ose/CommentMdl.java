package com.dnai.cedre.model.ose;

import org.simpleframework.xml.Text;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class CommentMdl {
	@Text(required=false)
	private String value;
}
