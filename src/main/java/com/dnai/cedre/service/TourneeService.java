package com.dnai.cedre.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.dao.AgentoseRepository;
import com.dnai.cedre.dao.CentreRepository;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.HistoriqueCodeOseRepository;
import com.dnai.cedre.dao.PrestationRepository;
import com.dnai.cedre.dao.TourneeAgentRepository;
import com.dnai.cedre.dao.TourneeRepository;
import com.dnai.cedre.dao.TranscoSignalementRepository;
import com.dnai.cedre.dao.VehiculeRepository;
import com.dnai.cedre.domain.Agentose;
import com.dnai.cedre.domain.Centre;
import com.dnai.cedre.domain.Clientose;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.HistoriqueCodeOse;
import com.dnai.cedre.domain.Prestation;
import com.dnai.cedre.domain.Tournee;
import com.dnai.cedre.domain.TourneeAgent;
import com.dnai.cedre.domain.TranscoSignalement;
import com.dnai.cedre.domain.Vehicule;
import com.dnai.cedre.model.AdresseMdl;
import com.dnai.cedre.model.CentreMdl;
import com.dnai.cedre.model.ClientMdl;
import com.dnai.cedre.model.ContactMdl;
import com.dnai.cedre.model.DetailEmportMdl;
import com.dnai.cedre.model.EmportretourMdl;
import com.dnai.cedre.model.OperationMdl;
import com.dnai.cedre.model.PjMdl;
import com.dnai.cedre.model.ServiceInfoMdl;
import com.dnai.cedre.model.SignalementMdl;
import com.dnai.cedre.model.TourneeInfoCritereMdl;
import com.dnai.cedre.model.TourneeInfoMdl;
import com.dnai.cedre.model.ose.AgtMdl;
import com.dnai.cedre.model.ose.CtMdl;
import com.dnai.cedre.model.ose.DispoMdl;
import com.dnai.cedre.model.ose.DocMdl;
import com.dnai.cedre.model.ose.GetSceResponseMdl;
import com.dnai.cedre.model.ose.OpMdl;
import com.dnai.cedre.model.ose.PcMdl;
import com.dnai.cedre.model.ose.SceTourneeMdl;
import com.dnai.cedre.util.Constantes;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TourneeService extends ParentService {

	@Autowired
	private HistoriqueCodeOseRepository historiqueCodeOseRepository;
	
	@Autowired
	private VehiculeRepository vehiculeRepository;
		
	@Autowired
	private OseService oseService;
	
	@Autowired
	private TourneeUtilService tourneeUtilService;
	
	@Autowired
	private CentreRepository centreRepository;
	
	@Autowired
	private AgentoseRepository agentoseRepository;
	
	@Autowired
	private TourneeAgentRepository tourneeAgentRepository;
	
	@Autowired
	private TourneeRepository tourneeRepository;
		
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private PrestationRepository prestationRepository;
		
	@Autowired
	private TranscoSignalementRepository transcoSignalementRepository;
	
	@Autowired
	private HistoriqueTourneeService historiqueTourneeService;
	
	@Autowired
	private DeroulementService deroulementService;
	
	/**
	 * Renvoie le service contenant une ou plusieurs tournées
	 * Stocke l'arbre XML du service OSE dans DynDb
	 * 
	 * @param tourneeInfoCritereMdl
	 * @param token
	 * @return
	 */
	public ServiceInfoMdl serviceInfo(TourneeInfoCritereMdl tourneeInfoCritereMdl, String token, String version) {
		ServiceInfoMdl serviceInfoMdl = null;
		try {
			String result = oseService.getFluxOse(tourneeInfoCritereMdl.getSid());
			
			GetSceResponseMdl getSceResponseMdl = oseService.getSceResponse(result);
			
			if(getSceResponseMdl!=null && getSceResponseMdl.getSce()!=null && !getSceResponseMdl.getSce().isEmpty()) {
				serviceInfoMdl = new ServiceInfoMdl();
				serviceInfoMdl.setDatefmt(getSceResponseMdl.getSce().get(0).getDate());
				serviceInfoMdl.setService(tourneeInfoCritereMdl.getService());
				serviceInfoMdl.setToken(token);
				serviceInfoMdl.setCodeservice(tourneeInfoCritereMdl.getSid());
				
				for(SceTourneeMdl sceTournee : getSceResponseMdl.getSce()) {
					TourneeInfoMdl tourneeInfoMdl = construitTourneeInfo(sceTournee);
					tourneeInfoMdl.setClos(isTourClos(tourneeInfoCritereMdl.getSid().toLowerCase(), sceTournee.getTour(), sceTournee.getId()));
					serviceInfoMdl.getTournees().add(tourneeInfoMdl);
				}
				
				String clefluxose = oseService.stockeFluxOse(result, tourneeInfoCritereMdl.getSid());
				
				// deroulementService.initDeroulement(token, version, tourneeInfoCritereMdl.getSid(), getSceResponseMdl);
				deroulementService.initDeroulement(token, version, tourneeInfoCritereMdl.getSid(), clefluxose);
				
				serviceInfoMdl = historiqueTourneeService.detecteChangements(serviceInfoMdl,tourneeInfoCritereMdl.getSid());
			}else {
				log.debug("getSceResponseMdl ou getSceResponseMdl.getSce() null, tourneeInfoCritereMdl : {}, result {}",tourneeInfoCritereMdl,result);
			}
		}catch(Exception e) {
			log.error("serviceInfo",e);
		}
		return serviceInfoMdl;
	}
	
	private ClientMdl construitClientMdl(final PcMdl pc, final int i, final String idtournee, final String tour, final String tourorigine) {
		AdresseMdl adresseMdl = oseService.construitAdresse(pc.getLoc());

		ClientMdl client = new ClientMdl();
		client.setId(pc.getId());
		client.setTid(idtournee);
		client.setTour(tour);
		client.setTourorigine(tourorigine);
		client.setEtat(ClientMdl.Etat.ATTENTE.toString());
		client.setNom(pc.getNom());
		client.setAdr1(adresseMdl.getAdr1());
		client.setAdr2(adresseMdl.getAdr2());
		client.setLatitude(adresseMdl.getLatitude());
		client.setLongitude(adresseMdl.getLongitude());
		client.setCodepostal(adresseMdl.getCodepostal());
		client.setVille(adresseMdl.getVille());
		client.setNumero(i);
		client.setMandant(pc.getMandant()!=null?pc.getMandant():"0");
		client.setHoraires(pc.getConsignes().getHoraires());
		client.setAcces(pc.getConsignes().getAdresseacces());
		client.setContact(pc.getConsignes().getContact());
		client.setRecommandation(pc.getConsignes().getRecommandations());
		client.setNumavis(idtournee + "." + i);
		client.setInfocovid(tourneeUtilService.calculInfocovid(pc.getOperations()));
		
		// signalements
		List<TranscoSignalement> signalements = transcoSignalementRepository.findByIdmandantAndActif(parseInt(pc.getMandant()), true);
		for(TranscoSignalement transcoSignalement : signalements) {
			SignalementMdl signalementMdl = new SignalementMdl();
			signalementMdl.setIdose(transcoSignalement.getIdcedre());
			signalementMdl.setLibelle(transcoSignalement.getTitre());
			client.getSignalements().add(signalementMdl);
		}
		
		for(OpMdl op : pc.getOperations().getOp()) {
			if(op.getDispo()!=null && !op.getDispo().isEmpty()) {
				for(DispoMdl dispo : op.getDispo()) {
					OperationMdl operationMdl = new OperationMdl();
					operationMdl.setId(op.getId());
					operationMdl.setDispoid(dispo.getId()!=null?dispo.getId():"0");
					operationMdl.setConsigne(tourneeUtilService.formatConsigne(dispo));
					operationMdl.setLibelle(op.getNom());
					operationMdl.setNom(dispo.getNom());
					operationMdl.setType(op.getType());
					operationMdl.setDechet(dispo.getDechet()!=null?dispo.getDechet():"");
					operationMdl.setQteprev(traitementQteOse(dispo.getQteprev()));
					operationMdl.setQtereel("0");
					operationMdl.setPdsreel(parsePoids(dispo.getPdsreel()));
					operationMdl.setDepotfourniture(isDepotfourniture(op));
					operationMdl.setCodetype(tourneeUtilService.calculCodetype(op));
					operationMdl.setTare(parseDouble(dispo.getTare()));
					operationMdl.setCleose(tourneeUtilService.genereCleOseOperation(op, dispo));
					client.getOperations().add(operationMdl);
				}
			}
		}
		
		if(pc.getDocs()!=null) {
			for(DocMdl docMdl : pc.getDocs()) {
				PjMdl pjMdl = new PjMdl();
				pjMdl.setUrl(docMdl.getUrl());
				pjMdl.setNom(FilenameUtils.getName(docMdl.getUrl()));
				client.getPieces().add(pjMdl);
			}
		}
		
		if(pc.getCtc()!=null) {
			for(CtMdl ct : pc.getCtc()) {
				String telfmt = formatTel(ct.getTel());
				if(telfmt!=null) {
					ContactMdl contactMdl = new ContactMdl();
					contactMdl.setFonction(ct.getFonction());
					contactMdl.setId(ct.getId());
					contactMdl.setTelfmt(telfmt);
					contactMdl.setTel(ct.getTel());
					contactMdl.setTitre(ct.getTitre());
					client.getContacts().add(contactMdl);
				}
			}
		}
		
		// extraction num tel de la donnée contact
		String telextrait = extraitNumTel(client.getContact());
		if(telextrait!=null) {
			String telfmt = formatTel(telextrait);
			if(telfmt!=null) {
				ContactMdl contactMdl = new ContactMdl();
				contactMdl.setTelfmt(telfmt);
				contactMdl.setTel(telfmt);
				contactMdl.setTitre(client.getContact());
				client.getContacts().add(contactMdl);
			}
		}
		return client;
	}
	
	public TourneeInfoMdl construitTourneeInfo(SceTourneeMdl sceTournee) {
		TourneeInfoMdl tourneeInfoMdl = new TourneeInfoMdl();
		tourneeInfoMdl.setDatefmt(sceTournee.getDate());
		tourneeInfoMdl.setIdentifiant(sceTournee.getId());
		tourneeInfoMdl.setTour(sceTournee.getTour());
		tourneeInfoMdl.setVehicule(oseService.extraitVehicule(sceTournee));
		tourneeInfoMdl.setNbclients(calculNbClients(sceTournee));
		tourneeInfoMdl.setEmportretours(construitListMarchandises(sceTournee));
		tourneeInfoMdl.setNotes(sceTournee.getNotes()!=null?sceTournee.getNotes().getValue():null);
		tourneeInfoMdl.setPesee(isPeseeEmbarquee(tourneeInfoMdl.getVehicule()));
		
		int i=1;
		if(sceTournee.getDessertes()!=null && sceTournee.getDessertes().getPc()!=null) {
			for(PcMdl pc : sceTournee.getDessertes().getPc()) {
				ClientMdl client = construitClientMdl(pc, i, tourneeInfoMdl.getIdentifiant(),
						tourneeInfoMdl.getTour(), tourneeInfoMdl.getTour());			
				tourneeInfoMdl.getClients().add(client);
				i++;
			}
		}else {
			log.warn("construitTourneeInfo : pc null pour sceTournee : " + sceTournee);
		}
		
		return tourneeInfoMdl;
	}
	
	private int calculNbClients(SceTourneeMdl sceTournee) {
		int nbClients = 0;
		try {
			if(sceTournee.getDessertes().getPc()!=null) {
				nbClients = sceTournee.getDessertes().getPc().size();
			}else {
				log.warn("calculNbClients : pc null pour sceTournee : {}",sceTournee);
			}
		} catch (Exception e) {
			log.error("calculNbClients : sceTournee {}",sceTournee,e);
		}
		return nbClients;
	}
	
	private List<EmportretourMdl> construitListMarchandises(SceTourneeMdl sceTournee){
		List<EmportretourMdl> listMarchandises = new ArrayList<>();
		try {			
			Map<String,Map<String,DetailEmportMdl>> mapEmportRetourDechet = new HashMap<>();
			if(sceTournee.getDessertes().getPc()!=null) {
				for(PcMdl pc : sceTournee.getDessertes().getPc()) {
					for(OpMdl op : pc.getOperations().getOp()) {
						if(op.getDispo()!=null && !op.getDispo().isEmpty()) {
							for(DispoMdl dispo : op.getDispo()) {
								String nomDispo = (Strings.isNullOrEmpty(dispo.getNom())?op.getNom():dispo.getNom());
								Map<String,DetailEmportMdl> mapDechetDispo = mapEmportRetourDechet.get(nomDispo);
								if(mapDechetDispo==null) {
									mapDechetDispo = new HashMap<>();
								}
								String libOperation = (Strings.isNullOrEmpty(dispo.getNom())?op.getNom():dispo.getNom());
								if(!Strings.isNullOrEmpty(libOperation)) {
									DetailEmportMdl detailEmportMdl = mapDechetDispo.get(dispo.getDechet());
									int quantite = traitementQteOseToNum(dispo.getQteprev());
									if(detailEmportMdl==null) {
										detailEmportMdl = new DetailEmportMdl();
										detailEmportMdl.setLibelle(libOperation);
										detailEmportMdl.setDechet(dispo.getDechet());
										detailEmportMdl.setPrestation(op.getPrestation());
									}
									detailEmportMdl = completeQte(detailEmportMdl, quantite, op.getPrestation());
									mapDechetDispo.put(dispo.getDechet(), detailEmportMdl);
									mapEmportRetourDechet.put(nomDispo, mapDechetDispo);
								}
							}
						}
					}
				}
			}else {
				log.warn("construitListMarchandises : pc null pour sceTournee : " + sceTournee);
			}
			
			Iterator<Map<String,DetailEmportMdl>> itEmportRetourDechet = mapEmportRetourDechet.values().iterator();
			while(itEmportRetourDechet.hasNext()) {
				List<DetailEmportMdl> listDetailEmport = new ArrayList<>(itEmportRetourDechet.next().values());
				for(DetailEmportMdl detailEmportMdl : listDetailEmport) {
					EmportretourMdl emportretourDetailMdl = new EmportretourMdl();
					emportretourDetailMdl.setLibelle(calculLibelleEmportretour(detailEmportMdl));
					emportretourDetailMdl.setDechet(detailEmportMdl.getDechet());
					emportretourDetailMdl.setImportant(calculImportantEmportretour(detailEmportMdl));
		
					emportretourDetailMdl.setAlivrer(detailEmportMdl.getQteALivrer()>0?String.valueOf(detailEmportMdl.getQteALivrer()):"");
					emportretourDetailMdl.setArecuperer(detailEmportMdl.getQteARecuperer()>0?String.valueOf(detailEmportMdl.getQteARecuperer()):"");

					listMarchandises.add(emportretourDetailMdl);
				}
			}
			Collections.sort(listMarchandises);
		} catch (Exception e) {
			log.error("construitListMarchandises : " + e.toString() + " avec sceTournee : " + sceTournee);
		}
		return listMarchandises;
	}

	
	private String calculLibelleEmportretour(DetailEmportMdl detailEmportMdl) {
		if(Constantes.PRESTATION_COLLECTE.equals(detailEmportMdl.getPrestation()) && 
				detailEmportMdl.getLibelle()!=null && (detailEmportMdl.getLibelle().toLowerCase().contains(Constantes.DISPO_CORBEILLE) || 
						detailEmportMdl.getLibelle().toLowerCase().contains(Constantes.DISPO_BONBONNE))) {
			return Constantes.DISPO_SAC;
		} else {
			return detailEmportMdl.getLibelle();
		}
	}
	
	private boolean calculImportantEmportretour(DetailEmportMdl detailEmportMdl) {
		if((Constantes.PRESTATION_DEPOT.equals(detailEmportMdl.getPrestation()) || 
				Constantes.PRESTATION_FOURNITURE.equals(detailEmportMdl.getPrestation())) && 
				detailEmportMdl.getLibelle()!=null && detailEmportMdl.getLibelle().toLowerCase().contains(Constantes.DISPO_CORBEILLE) || 
						detailEmportMdl.getLibelle().toLowerCase().contains(Constantes.DISPO_BONBONNE)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private DetailEmportMdl completeQte(DetailEmportMdl detailEmportMdl, int quantite, String prestation) {
		if(Constantes.PRESTATION_COLLECTE.equals(prestation) || 
				Constantes.PRESTATION_DEPOTRETRAIT.equals(prestation)) {
			detailEmportMdl.setQteALivrer(detailEmportMdl.getQteALivrer() + quantite);
			detailEmportMdl.setQteARecuperer(detailEmportMdl.getQteARecuperer() + quantite);
		} else if(Constantes.PRESTATION_DEPOT.equals(prestation) || 
				Constantes.PRESTATION_FOURNITURE.equals(prestation)) {
			detailEmportMdl.setQteALivrer(detailEmportMdl.getQteALivrer() + quantite);
		} else if(Constantes.PRESTATION_RETRAIT.equals(prestation)) {
			detailEmportMdl.setQteARecuperer(detailEmportMdl.getQteARecuperer() + quantite);
		} else {
			detailEmportMdl.setQteALivrer(detailEmportMdl.getQteALivrer() + quantite);
			detailEmportMdl.setQteARecuperer(detailEmportMdl.getQteARecuperer() + quantite);
		}
		return detailEmportMdl;
	}
		
	private SceTourneeMdl calculProchaineTourneeId(GetSceResponseMdl getSceResponseMdl, int idxTournee) {
		SceTourneeMdl prochaineTournee = null;
		try {
			List<SceTourneeMdl> listSceTourneeMdl = getSceResponseMdl.getSce();
			if(listSceTourneeMdl.size()>idxTournee+1) {
				prochaineTournee = listSceTourneeMdl.get(idxTournee+1);
			}
		}catch(Exception e) {
			log.error("calculProchaineTourneeId : " + e.toString());
		}
		return prochaineTournee;
	}
	
	public TourneeInfoMdl preparationProchaineTournee(TourneeInfoCritereMdl tourneeInfoCritereMdl, String token) {
		TourneeInfoMdl tourneeInfoMdl = null;
		try {
			if(tourneeInfoCritereMdl!=null && tourneeInfoCritereMdl.getId()!=null) {
				GetSceResponseMdl getSceResponseMdl = oseService.getServiceOse(token);
				SceTourneeMdl sceTournee = getSceTourneeMdlByTour(tourneeInfoCritereMdl.getTour(), tourneeInfoCritereMdl.getId(), getSceResponseMdl);
				if(sceTournee!=null) {
					tourneeInfoMdl = construitTourneeInfo(sceTournee);
				}
			}
		}catch(Exception e) {
			log.error("preparationProchaineTournee : " + e.toString());
		}
		return tourneeInfoMdl;
	}
	
	private SceTourneeMdl getSceTourneeMdlByTour(final String tour, final String idtournee, final GetSceResponseMdl getSceResponseMdl) {
		SceTourneeMdl sceTournee = null;
		for(SceTourneeMdl sceTourneeTemp : getSceResponseMdl.getSce()) {
			if(sceTourneeTemp.getId().equals(idtournee)
					&& sceTourneeTemp.getTour().equals(tour)) {
				sceTournee = sceTourneeTemp;
				break;
			}
		}
		return sceTournee;
	}
	
	/**
	 * tournée sélectionnée dans le service
	 * 
	 * @param tourneeInfoCritereMdl
	 * @param token
	 * @return
	 */
	public TourneeInfoMdl tourneeInfo(TourneeInfoCritereMdl tourneeInfoCritereMdl, GetSceResponseMdl getSceResponseMdl, String token) {
		TourneeInfoMdl tourneeInfoMdl = null;
		try {
			if(tourneeInfoCritereMdl!=null && tourneeInfoCritereMdl.getId()!=null) {
				SceTourneeMdl sceTournee = null;
				int idxTournee = 0;
				for(SceTourneeMdl sceTourneeTemp : getSceResponseMdl.getSce()) {
					if(sceTourneeTemp.getId().equals(tourneeInfoCritereMdl.getId())
							&& sceTourneeTemp.getTour().equals(tourneeInfoCritereMdl.getTour())) {
						sceTournee = sceTourneeTemp;
						deroulementService.majTourneeId(token, sceTourneeTemp.getId());
						// contexteService.majTourneeId(sceTourneeTemp.getId(), token);
						break;
					}
					idxTournee++;
				}
				if(sceTournee!=null) {
					tourneeInfoMdl = construitTourneeInfo(sceTournee);
					tourneeInfoMdl.setCodeservice(tourneeInfoCritereMdl.getCodeservice());
					SceTourneeMdl prochaineTournee = calculProchaineTourneeId(getSceResponseMdl, idxTournee);
					if(prochaineTournee!=null) {
						tourneeInfoMdl.setProchaineTourneeId(prochaineTournee.getId());
						tourneeInfoMdl.setProchainTour(prochaineTournee.getTour());
					}
				}
			}
		}catch(Exception e) {
			log.error("tourneeInfo : " + e.toString());
		}
		return tourneeInfoMdl;
	}
	
	private boolean isDepotfourniture(OpMdl op) {
		boolean isDepotfourniture = false;
		String prestation = op!=null?op.getPrestation():"";
		if(prestation!=null && (prestation.equals(Constantes.PRESTATION_DEPOT) 
				|| prestation.equals(Constantes.PRESTATION_FOURNITURE))) {
			isDepotfourniture = true;
		}
		return isDepotfourniture;
	}
	
	@Transactional
	private boolean isTourClos(String codeservice, String tour, String idose) {
		boolean isTourClos = false;
		LocalDate localDateJour = LocalDate.now();
		Date dateJour = Date.from(localDateJour.atStartOfDay(ZoneId.systemDefault()).toInstant());
		HistoriqueCodeOse historiqueCodeOse = historiqueCodeOseRepository.findFirstByCodeserviceAndTourAndIdoseAndDatetournee(codeservice.toUpperCase(), tour, idose, dateJour);
		if(historiqueCodeOse!=null) {
			isTourClos = true;
			log.warn("isTourClos : historiqueCodeOse non null : " + historiqueCodeOse);
		}
		return isTourClos;
	}
	
	@Transactional
	private boolean isPeseeEmbarquee(String immatriculation) {
		Vehicule vehicule = vehiculeRepository.findFirstByImmatriculation(immatriculation);
		return (vehicule!=null && vehicule.isPesee());
	}

	@Transactional
	public TourneeInfoMdl tourneeFusion(final TourneeInfoMdl tourneeMdl, final GetSceResponseMdl getSceResponseMdl) {
		TourneeInfoMdl tourneeInfoMdl = null;
		try {
			tourneeInfoMdl = tourneeMdl;
			
			String tourSuivant = String.valueOf(Integer.valueOf(tourneeMdl.getTour()) + 1);
			SceTourneeMdl sceTournee = getSceTourneeMdlByTour(tourSuivant, tourneeMdl.getIdentifiant(), getSceResponseMdl);
			if(sceTournee!=null) {
				if(sceTournee.getDessertes()!=null && sceTournee.getDessertes().getPc()!=null) {
					int notourorigine = parseInt(sceTournee.getTour());
					int i = tourneeMdl.getClients().size() + 1;
					int nbAgents = sceTournee.getRessources().getAgts().getAgt().size();
					Tournee tournee = tourneeRepository.findById(tourneeMdl.getIdadnai()).get();
					for(PcMdl pc : sceTournee.getDessertes().getPc()) {
						ClientMdl clientMdl = construitClientMdl(pc, i, tourneeMdl.getIdentifiant(), tourneeMdl.getTour(), sceTournee.getTour());
						
						Clientose clientose = creerClientose(clientMdl);
						Collecte collecte = creerCollecte(false, tournee, clientose, clientMdl,notourorigine);
						clientMdl.setIdcollecteadnai(collecte.getId());
						clientMdl.setNbagents(nbAgents);
						clientMdl.setMiseajour(true);
						
						tourneeInfoMdl.getClients().add(clientMdl);
						i++;
					}
					tournee.setNombretour(tournee.getNombretour()-1);
					tournee.setFusion(true);
					tourneeRepository.save(tournee);
				}
				tourneeInfoMdl.setNbclients(tourneeInfoMdl.getClients().size());
				tourneeInfoMdl.setFusion(true);
			}else {
				log.error("tourneeFusion sceTournee null {}, {}",tourneeMdl,getSceResponseMdl);
			}
		}catch(Exception e) {
			tourneeInfoMdl = tourneeMdl;
			log.error("tourneeFusion {}",tourneeMdl,e);
		}
		return tourneeInfoMdl;
	}
	
	@Transactional
	public TourneeInfoMdl creerTournee(TourneeInfoMdl tourneeInfoMdl, GetSceResponseMdl getSceResponseMdl) {
		try {
			SceTourneeMdl sceTourneeMdl = tourneeUtilService.extraitTourCourant(getSceResponseMdl, tourneeInfoMdl.getIdentifiant(), tourneeInfoMdl.getTour());
			int nombretour = nombreTour(getSceResponseMdl);
			
			int notour = parseInt(tourneeInfoMdl.getTour());
			Date datetournee = tourneeUtilService.extraitDateTournee(tourneeInfoMdl.getDatefmt());
			
			if(sceTourneeMdl!=null) {
				boolean isTourneeExistante = true;
				Tournee tournee = tourneeRepository.findFirstByDatetourneeAndNotourAndIdoseAndTypetournee(datetournee,notour,tourneeInfoMdl.getIdentifiant(), Tournee.Typetournee.COLLECTE.toString());
				if(tournee==null || isDevProfile() || tourneeInfoMdl.getIdentifiant().startsWith(Constantes.PREFIX_TOURNEE_ADNAITEST)) {
					Tournee tourneeNew = new Tournee();
					tourneeNew.setDhdebut(new Date());
					tourneeNew.setDatetournee(datetournee);
					tourneeNew.setVehicule(tourneeInfoMdl.getVehicule());
					tourneeNew.setNotour(notour);
					tourneeNew.setNombretour(nombretour);
					tourneeNew.setIdose(tourneeInfoMdl.getIdentifiant());
					tourneeNew.setLibequipe(tourneeUtilService.extraitLibequipe(sceTourneeMdl));
					tourneeNew.setIdcentre(tourneeUtilService.extraitCentreId(sceTourneeMdl));
					tourneeNew.setEtat(Constantes.TOURNEE_ETAT_DEBUT);
					tourneeNew.setCodeservice(tourneeInfoMdl.getCodeservice());
					tourneeNew.setToken(tourneeInfoMdl.getToken());
					tourneeNew.setTypetournee(Tournee.Typetournee.COLLECTE.toString());
					tournee = tourneeRepository.save(tourneeNew);
					isTourneeExistante = false;
				}else {
					tournee.setDhdebut(new Date());
					tournee.setToken(tourneeInfoMdl.getToken());
					tourneeRepository.save(tournee);
					log.warn("creerTournee : tournee existante pour tourneeInfoMdl : " + tourneeInfoMdl);
				}
				tourneeInfoMdl.setDebut(tournee.getDhdebut().getTime());
				tourneeInfoMdl.setIdadnai(tournee.getId());
				
				// Agents
				List<AgtMdl> agents = sceTourneeMdl.getRessources().getAgts().getAgt();
				int nbAgents = agents.size();
				tourneeInfoMdl.setNbagents(nbAgents);
				
				if(!isTourneeExistante) {
					for(AgtMdl agtMdl : agents) {
						Agentose agentose = agentoseRepository.findFirstByIdose(agtMdl.getId());
						if(agentose==null) {
							Agentose agentosenew = new Agentose();
							agentosenew.setIdose(agtMdl.getId());
							agentosenew.setNom(agtMdl.getNom());
							agentose = agentoseRepository.save(agentosenew);
						}
						tourneeAgentRepository.save(new TourneeAgent(tournee.getId(),agentose.getId()));
					}
				}
				
				for(ClientMdl clientMdl : tourneeInfoMdl.getClients()) {
					clientMdl.setIdtourneeadnai(tournee.getId());
					
					Clientose clientose = creerClientose(clientMdl);
					
					Collecte collecte = creerCollecte(isTourneeExistante, tournee, clientose, clientMdl, notour);
					log.debug("collecte {}",collecte);
					clientMdl.setIdcollecteadnai(collecte.getId());
					clientMdl.setNbagents(nbAgents);
				}
				
				Optional<Centre> optCentre = centreRepository.findById(Integer.valueOf(tournee.getIdcentre()).longValue());
				if(optCentre.isPresent()) {
					Centre centre = optCentre.get();
					//if(centre!=null) {
					CentreMdl centreMdl = new CentreMdl();
					centreMdl.setLatitude(centre.getLatitude());
					centreMdl.setLongitude(centre.getLongitude());
					centreMdl.setNom(centre.getNom());
					tourneeInfoMdl.setCentre(centreMdl);
				}else {
					log.warn("creerTournee : centre null pour idcentre : {}",tournee.getIdcentre());
				}
			}else {
				log.error("sceTourneeMdl null pour tourneeInfoMdl : " + tourneeInfoMdl);
			}
			
		}catch(Exception e) {
			log.error("creerTournee : tourneeInfoMdl : {}, getSceResponseMdl : {}",tourneeInfoMdl,getSceResponseMdl,e);
		}
		return tourneeInfoMdl;
	}
		
	@Transactional
	private Collecte creerCollecte(final boolean isTourneeExistante, final Tournee tournee, final Clientose clientose, final ClientMdl clientMdl, final int notourorigine) {
		Collecte collecte = null;
		try {
			List<OperationMdl> listOperationMdlMaj = new ArrayList<>();
			if(isTourneeExistante) {
				collecte = collecteRepository.findFirstByTourneeAndClientose(tournee, clientose);
				if(collecte==null) {
					Collecte collecteNew = new Collecte();
					collecteNew.setClientose(clientose);
					collecteNew.setTournee(tournee);
					collecteNew.setEtat(Constantes.COLLECTE_ETAT_ATTENTE);
					collecteNew.setMandant(Integer.valueOf(clientMdl.getMandant()));
					collecteNew.setNotourorigine(notourorigine);
					collecte = collecteRepository.save(collecteNew);
				}else {
					collecte.setNotourorigine(notourorigine);
					collecteRepository.save(collecte);	
				}
			}
			
			if(collecte!=null) {
				int position = 0;
				for(OperationMdl operationMdl : clientMdl.getOperations()) {
					Prestation prestation = prestationRepository.findFirstByCollecteAndPosition(collecte, position);							
					if(prestation==null) {
						prestation = creerPrestation(operationMdl, collecte, position);
					}
					if(prestation!=null) {
						operationMdl.setIdprestationadnai(prestation.getId());
					}
					listOperationMdlMaj.add(operationMdl);
					position++;
				}
			}else {
				Collecte collecteNew = new Collecte();
				collecteNew.setClientose(clientose);
				collecteNew.setTournee(tournee);
				collecteNew.setEtat(Constantes.COLLECTE_ETAT_ATTENTE);
				collecteNew.setMandant(Integer.valueOf(clientMdl.getMandant()));
				collecteNew.setNotourorigine(notourorigine);
				collecte = collecteRepository.save(collecteNew);
				
				int position = 0;
				for(OperationMdl operationMdl : clientMdl.getOperations()) {							
					Prestation prestation = creerPrestation(operationMdl, collecte, position);
					operationMdl.setIdprestationadnai(prestation.getId());
					listOperationMdlMaj.add(operationMdl);
					position++;
				}
				clientMdl.setOperations(listOperationMdlMaj);
			}
		}catch(Exception e) {
			log.error("creerCollecte : ",e);
		}
		return collecte;
	}
	
	@Transactional
	private Prestation creerPrestation(final OperationMdl operationMdl, final Collecte collecte, final int position) {
		Prestation prestation = null;
		try {
			Prestation	prestationNew = new Prestation();
			prestationNew.setCollecte(collecte);
			String libelle = contruitLibPrestation(operationMdl.getLibelle(), operationMdl.getNom());
			prestationNew.setLibelle(StringUtils.abbreviate(libelle,100));
			prestationNew.setQteabsent(operationMdl.getBacabsent());
			prestationNew.setQteprevu(traitementQteOse(operationMdl.getQteprev()));
			prestationNew.setQtereel(traitementQteOse(operationMdl.getQtereel()));
			prestationNew.setQtevide(operationMdl.getBacvide());
			prestationNew.setPoids(0);
			prestationNew.setPoidsparagent(0);
			prestationNew.setPosition(position);
			prestationNew.setOperation(StringUtils.abbreviate(operationMdl.getLibelle(),50));
			prestationNew.setDispositif(StringUtils.abbreviate(operationMdl.getNom(),50));
			prestationNew.setOpid(operationMdl.getId());
			prestationNew.setDispoid(operationMdl.getDispoid());
			prestationNew.setCleose(operationMdl.getCleose());
			prestation =  prestationRepository.save(prestationNew);
		}catch(Exception e) {
			log.error("creerCollecte : ",e);
		}
		return prestation;
	}
	
	private int nombreTour(final GetSceResponseMdl getSceResponseMdl) {
		int nombreTour = 0;
		try {
			nombreTour = getSceResponseMdl.getSce().size();
		}catch(Exception e) {
			log.error("nombreTour : getSceResponseMdl : {}",getSceResponseMdl,e);
		}
		return nombreTour;
	}
}
