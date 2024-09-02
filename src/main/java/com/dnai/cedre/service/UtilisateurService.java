package com.dnai.cedre.service;

import com.dnai.cedre.dao.UtilisateurRepository;
import com.dnai.cedre.domain.Utilisateur;
import com.dnai.cedre.model.cockpit.UtilisateurMdl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UtilisateurService extends ParentService{

	@Autowired
	private UtilisateurRepository utilisateurRepository;
	
	//@Autowired
	//private AdnAiUtilsService adnAiUtilsService;
	
	@Transactional
	public List<UtilisateurMdl> utilisateurs() {
		List<UtilisateurMdl> utilisateurs = new ArrayList<>();
		try {
			List<Utilisateur> liste = utilisateurRepository.findByMandantGreaterThanEqual(0);
			for(Utilisateur utilisateur : liste) {
				if(!Utilisateur.ROLE.ROLE_ADMIN.name().equals(utilisateur.getRole())) {
					UtilisateurMdl utilisateurListeMdl = UtilisateurMdl.builder()
							.email(utilisateur.getEmail())
							.id(utilisateur.getId())
							.mandant(transcoMandant(utilisateur.getMandant()))
							.idmandant(utilisateur.getMandant())
							.manager(Utilisateur.ROLE.ROLE_CEDRE_MANAGER.name().equals(utilisateur.getRole()))
							.build();
					utilisateurs.add(utilisateurListeMdl);
				}
			}
		}catch(Exception e) {
			log.error("utilisateurs",e);
		}
		return utilisateurs;
	}

	
	private String genereMotdepasse() {
		return RandomStringUtils.randomAlphanumeric(8);
	}
	
	@Transactional
	public boolean utilisateurExistant(final String email) {
		if(utilisateurRepository.findFirstByEmail(email)!=null) {
			return true;
		}else {
			return false;
		}
	}
	
	@Transactional
	public boolean delete(UtilisateurMdl utilisateurMdl) {
		boolean retour = false;
		try {
			Utilisateur utilisateur = utilisateurRepository.findById(utilisateurMdl.getId()).get();
			utilisateurRepository.delete(utilisateur);
			retour = true;
		} catch (Exception e) {
			log.error("delete, {}", utilisateurMdl, e);
		}
		return retour;
	}
	
	@Transactional
	public boolean enregistreUtilisateur(final UtilisateurMdl utilisateurMdl) {
		boolean retour = false;
		try {
			Utilisateur utilisateur = null;
			String motdepasse = null;
			if(utilisateurMdl.getId()==0) {
				utilisateur = new Utilisateur();
				
				if(utilisateurMdl.getIdmandant()>0) {
					motdepasse = genereMotdepasse();
					utilisateur.setPassword(BCrypt.hashpw(motdepasse, BCrypt.gensalt()));
				}
			}else {
				utilisateur = utilisateurRepository.findById(utilisateurMdl.getId()).get();
			}
			
			if(utilisateurMdl.getIdmandant()>0) {
				utilisateur.setRole(Utilisateur.ROLE.ROLE_CLIENT.name());
			}else {
				if(utilisateurMdl.isManager()) {
					utilisateur.setRole(Utilisateur.ROLE.ROLE_CEDRE_MANAGER.name());
				}else {
					utilisateur.setRole(Utilisateur.ROLE.ROLE_CEDRE.name());
				}
			}
			utilisateur.setEmail(utilisateurMdl.getEmail());
			utilisateur.setMandant(utilisateurMdl.getIdmandant());
			
			utilisateurRepository.save(utilisateur);
			
			if(utilisateurMdl.getId()==0 && utilisateurMdl.getIdmandant()>0) {
				envoiMailCreationCompte(motdepasse, utilisateur);
			}
			
			retour = true;
		} catch (Exception e) {
			log.error("enregistreUtilisateur, {}", utilisateurMdl, e);
		}
		return retour;
	}
	
	private String transcoMandant(final int idmandant) {
		String mandant = "";
		
		switch(idmandant) {
		case 0:
			mandant = "CEDRE";
			break;
		case 1:
			mandant = "VEOLIA";
			break;
		default:
			mandant = "";
		}
		return mandant;
	}
	
	private void envoiMailCreationCompte(String motdepasse, Utilisateur utilisateur) {
		try {
			/*EmailRequest emailRequest = new EmailRequest();
			emailRequest.setSubject(Constantes.CREATION_COMPTE_SUBJECT);
			emailRequest.setCodeconfiguration(Constantes.CODE_CONF_CREATION_COMPTE);
			emailRequest.getContenus().put("motdepasse",motdepasse);
			EmailDestinataire emailDestinataire = new EmailDestinataire();
			emailDestinataire.setRecipient(utilisateur.getEmail());
			emailDestinataire.setTypedest("TO");
			emailRequest.setDestinataires(Arrays.asList(emailDestinataire));
			EmailResult emailResult = adnAiUtilsService.sendEmail(emailRequest);
			if(emailResult.getCode()!=0) {
				log.error("erreur envoiMailCreationCompte {}",utilisateur);
			}*/
			log.info("envoiMailCreationCompte {}",utilisateur);
		}catch(Exception e) {
			log.error("envoiMailCreationCompte {}",utilisateur,e);
		}
	}
}
