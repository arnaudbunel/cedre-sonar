package com.dnai.cedre.model.dib;

import java.util.ArrayList;
import java.util.List;

import com.dnai.cedre.model.ClientCommunMdl;
import com.dnai.cedre.model.MediaMdl;

@lombok.Getter
@lombok.Setter
@lombok.ToString
public class ClientdibMdl extends ClientCommunMdl{
	private long dhpassage;
	private String adressefmt;
	private List<OperationdibMdl> operations = new ArrayList<>();
	private List<MediaMdl> sigltimage = new ArrayList<>();
	private String etatsync; // DEBUT ou FIN , utilisé pour la sync avec le server
}
