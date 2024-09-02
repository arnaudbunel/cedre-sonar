package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.UtilisateurRepository;
import com.dnai.cedre.domain.Utilisateur;
import com.dnai.cedre.model.cockpit.AuthentificationRetourMdl;
import com.dnai.cedre.model.cockpit.CredentialMdl;
import com.dnai.cedre.model.ose.ReponseAuthCockpitErreurMdl;
import com.dnai.cedre.model.ose.ReponseAuthCockpitOkMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthentificationCockpitService /*extends ParentService*/{

	@Autowired
	private Environment env;
	
	@Autowired
	private UtilisateurRepository utilisateurRepository;
	
	@Autowired
	private HttpClientBuilder httpClientBuilder;
	
	public AuthentificationRetourMdl authentificationCockpit(CredentialMdl credentialMdl) {
		AuthentificationRetourMdl authentificationRetourMdl = new AuthentificationRetourMdl();
		
		try {
			if(credentialMdl.getLogin().endsWith("@cedre.info")) {
				String endPoint = env.getProperty(Constantes.OSE_SERVICE_COCKPIT_LOGIN);
				CloseableHttpClient httpClient = httpClientBuilder.build();
				HttpPost httpPost = new HttpPost(endPoint);
				
				String xmldata = "<conn mail=\"" + credentialMdl.getLogin() + "\" pass=\"" + credentialMdl.getPassword() + "\"/>";
							
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("xmldata", xmldata));
				
				httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
				
			    HttpResponse response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				
				if(result!=null) {
					if(result.contains("reponse")) {
						authentificationRetourMdl.setAuthentifie(false);
						ReponseAuthCockpitErreurMdl reponseAuthCockpitErreurMdl = getReponseAuthCockpitErreur(result);
						authentificationRetourMdl.setMessage(reponseAuthCockpitErreurMdl.getValue());
					} else if(result.contains("conn")) {
						authentificationRetourMdl.setAuthentifie(true);
						ReponseAuthCockpitOkMdl reponseAuthCockpitOkMdl = getReponseAuthCockpitOk(result);
						authentificationRetourMdl.setUtilisateur(reponseAuthCockpitOkMdl.getMail());
						
						Utilisateur utilisateur = utilisateurRepository.findFirstByEmail(credentialMdl.getLogin());
						if(utilisateur!=null && utilisateur.getRole()!=null) {
							authentificationRetourMdl.setRole(utilisateur.getRole());
						}else {
							authentificationRetourMdl.setRole(Utilisateur.ROLE.ROLE_CEDRE.toString());
						}
					}
				}
				
				log.debug("authentificationRetourMdl : " + authentificationRetourMdl);
				// <?xml version="1.0" encoding="utf-8"?>
				// <reponse type="erreur">Identification erronée : réessayez SVP ou contactez votre administrateur</reponse>
				
				// <conn mail="...@..." pass="xxxx" status="OK"/>
			}else {
				Utilisateur utilisateur = utilisateurRepository.findFirstByEmail(credentialMdl.getLogin());
				if(utilisateur!=null && credentialMdl.getPassword()!=null &&
						BCrypt.checkpw(credentialMdl.getPassword(), utilisateur.getPassword())) {
					authentificationRetourMdl.setAuthentifie(true);
					authentificationRetourMdl.setUtilisateur(credentialMdl.getLogin());
					authentificationRetourMdl.setRole(utilisateur.getRole());
					authentificationRetourMdl.setIdutilisateur(utilisateur.getId());
					authentificationRetourMdl.setIdmandant(utilisateur.getMandant());
				}else {
					authentificationRetourMdl.setAuthentifie(false);
					authentificationRetourMdl.setMessage("identifiant ou mot de passe incorrect");
				}
			}
		}catch(Exception e) {
			log.error("authentificationCockpit : credentialMdl : {}",credentialMdl,e);
		}
		
		return authentificationRetourMdl;
	}
	
	private ReponseAuthCockpitErreurMdl getReponseAuthCockpitErreur(String result) {
		ReponseAuthCockpitErreurMdl reponseAuthCockpitErreur = null;
		try {
			if (result != null) {			
				Serializer serializer = new Persister();
				reponseAuthCockpitErreur = serializer.read(ReponseAuthCockpitErreurMdl.class, result, false);
			}
		} catch (Exception e) {
			log.error("getReponseAuthCockpitErreur : " + e.toString() + " avec result : " + result);
		}
		return reponseAuthCockpitErreur;
	}
	
	private ReponseAuthCockpitOkMdl getReponseAuthCockpitOk(String result) {
		ReponseAuthCockpitOkMdl reponseAuthCockpitOk = null;
		try {
			if (result != null) {			
				Serializer serializer = new Persister();
				reponseAuthCockpitOk = serializer.read(ReponseAuthCockpitOkMdl.class, result, false);
			}
		} catch (Exception e) {
			log.error("getReponseAuthCockpitOk : " + e.toString() + " avec result : " + result);
		}
		return reponseAuthCockpitOk;
	}
}
