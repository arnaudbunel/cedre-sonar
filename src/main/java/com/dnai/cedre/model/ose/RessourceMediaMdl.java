package com.dnai.cedre.model.ose;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class RessourceMediaMdl {
	@Attribute(required=false)
	private String type;
	@Attribute(required=false)
	private String url;
	@Attribute(required=false)
	private String dateheure; // timestamp
	
	@Attribute(required=false)
	private String id;
	
	@Attribute(required=false)
	private String compo;
	
	@Element(required=false)
	private CommentMdl comment;
	
	@ElementList(entry="image", inline=true, required=false)
	private List<ImageMdl> image;
	
	@ElementList(entry="audio", inline=true, required=false)
	private List<AudioMdl> audio;
	
	public enum Type {SIGNATURE,AUDIO,IMAGE,TEXTE,AVISDEPASSAGE,SIGNALEMENT,AVPIMAGE};
}
