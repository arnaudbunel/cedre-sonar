package com.dnai.cedre.controller.cockpit;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnai.cedre.model.ResultGenericMdl;
import com.dnai.cedre.model.cockpit.UtilisateurMdl;
import com.dnai.cedre.service.UtilisateurService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CockpitUtilisateurController {
	
	@Autowired
	private UtilisateurService utilisateurService;
	
	@GetMapping(value = "utilisateurs")
	public List<UtilisateurMdl> utilisateurs(HttpServletResponse httpServletResponse){
		try {			
			return utilisateurService.utilisateurs();
		}catch (Exception e) {
			log.error("utilisateurs",e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@PostMapping(value = "deleteutilisateur",  consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl deleteUtilisateur(@RequestBody final UtilisateurMdl utilisateurDetailModel, final HttpServletRequest httpServletRequest,final HttpServletResponse httpServletResponse){
		ResultGenericMdl result = new ResultGenericMdl();
		try {
			boolean retour = utilisateurService.delete(utilisateurDetailModel);
			if(retour) {
				result.setCode(HttpServletResponse.SC_OK);
			}else {
				result.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setMessage("Erreur lors de la suppression de l'utilisateur");
			}
		} catch (Exception e) {
			log.error("deleteUtilisateur",e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result.setMessage("Erreur lors de la suppression de l'utilisateur");
		}
		return result;
	}
	
	@PostMapping(value = "enregistreutilisateur",  consumes = "application/json; charset=UTF-8")
	public ResultGenericMdl enregistreUtilisateur(@RequestBody final UtilisateurMdl utilisateurDetailModel, final HttpServletRequest httpServletRequest,final HttpServletResponse httpServletResponse){
		ResultGenericMdl result = new ResultGenericMdl();
		try {
			if(utilisateurDetailModel.getId()==0 && utilisateurService.utilisateurExistant(utilisateurDetailModel.getEmail())) {
				result.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result.setMessage("cet utilisateur existe déjà");
			}else {
				boolean retour = utilisateurService.enregistreUtilisateur(utilisateurDetailModel);
				if(retour) {
					result.setCode(HttpServletResponse.SC_OK);
				}else {
					result.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					result.setMessage("Erreur lors de la sauvegarde de l'utilisateur");
				}
			}

		} catch (Exception e) {
			log.error("enregistreUtilisateur",e);
			httpServletResponse.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result.setMessage("Erreur lors de la sauvegarde de l'utilisateur");
		}
		return result;
	}
}
