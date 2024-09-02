package com.dnai.cedre.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.model.*;
import com.dnai.cedre.dao.*;
import com.dnai.cedre.domain.*;
import com.dnai.cedre.model.*;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.dnai.cedre.model.ose.AMdl;
import com.dnai.cedre.model.ose.AudioMdl;
import com.dnai.cedre.model.ose.CommentMdl;
import com.dnai.cedre.model.ose.DispoMdl;
import com.dnai.cedre.model.ose.GetSceResponseMdl;
import com.dnai.cedre.model.ose.ImageMdl;
import com.dnai.cedre.model.ose.LocMdl;
import com.dnai.cedre.model.ose.OpMdl;
import com.dnai.cedre.model.ose.PcMdl;
import com.dnai.cedre.model.ose.ReceptMdl;
import com.dnai.cedre.model.ose.RessourceMediaMdl;
import com.dnai.cedre.model.ose.SceTourneeMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OseService extends ParentService{

	@Autowired
	private Environment env;
		
	@Autowired
	private MediaService mediaService;
		
	@Autowired
	private HistoriqueCodeOseService historiqueCodeOseService;
	
	@Autowired
	private HttpClientBuilder httpClientBuilder;
	
	//@Autowired
	//private DeroulementService deroulementService;
	
	@Autowired
	private TourneeUtilService tourneeUtilService;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Autowired
	private CollecteDibRepository collecteDibRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
	
	@Autowired
	private PrestationDibRepository prestationDibRepository;
	
	@Autowired
	private PrestationRepository prestationRepository;
	
	@Autowired
	private MediaDibRepository mediaDibRepository;
	
	@Autowired
	private TranscoSignalementRepository transcoSignalementRepository;
	
	@Autowired
	private TourneeRepository tourneeRepository;

	@Autowired
	private MediaRepository mediaRepository;

	@Autowired
	private CommonUtilService commonUtilService;

	@Autowired
	@Setter(value = AccessLevel.PROTECTED)
	private DynamoDBMapper mapper;

	/*
	 * 1. Début du tour
2. Arrivée chez le client
3. Départ du client
...Itération au point 2 sur plusieurs clients.
4. Fin du tour
	 */
	private GetSceResponseMdl filtreGetSceResponseMdlParTour(GetSceResponseMdl getSceResponseMdl, String tour) {
		GetSceResponseMdl getSceResponseMdlFiltree = getSceResponseMdl;
		try {
			List<SceTourneeMdl> scesfiltree = new ArrayList<>();
			List<SceTourneeMdl> sces = getSceResponseMdl.getSce();
			for(SceTourneeMdl sce : sces) {
				if(sce.getTour() != null && sce.getTour().equals(tour)) {
					scesfiltree.add(sce);
					break;
				}
			}
			if(!scesfiltree.isEmpty()) {
				getSceResponseMdlFiltree.setSce(scesfiltree);
			}
		}catch(Exception e) {
			log.error("filtreGetSceResponseMdlParTour : getSceResponseMdl {}, tour {}",getSceResponseMdl,tour,e);
		}
		return getSceResponseMdlFiltree;
	}
	
	/**
	 * mise à jour dans l'arbre xml OSE de :
	 * - attribut start
	 * - attributs lat et lng
	 * 
	 * @param clientMdl
	 * @param token
	 */
	public void majDebutClient(ClientMdl clientMdl, String token) {
		try {
			GetSceResponseMdl getSceResponseMdl = getServiceOse(token);
			
			if(getSceResponseMdl!=null) {
				for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
					if(isMemeTournee(sceTourneeMdl,clientMdl)) {
						for(PcMdl pcMdl : sceTourneeMdl.getDessertes().getPc()) {
							if(clientMdl.getId().equals(pcMdl.getId())) {
								pcMdl.getExe().setStart(longToDateTimeFmt(clientMdl.getDebut()));
								pcMdl.getLoc().setLat(String.valueOf(clientMdl.getLatitude()));
								pcMdl.getLoc().setLng(String.valueOf(clientMdl.getLongitude()));
								pcMdl.setStatus(PcMdl.Status.ENCOURS.toString());
							}
						}
					}
				}
				majDeroulementTourneeMdl(token, null, getSceResponseMdl,Constantes.DRL_CTX_DEBUT_CLIENT);
				
				// on ne fait plus les put ose intermédiaires
				//if(!isAdnaitest(getSceResponseMdl)) {
					//putService(getSceResponseMdl);
					//putService(filtreGetSceResponseMdlParTour(getSceResponseMdl, clientMdl.getTourorigine()));
				//}
			}else {
				log.error("majDebutClient : getSceResponseMdl null pour token : " + token + ", et clientMdl : " + clientMdl);
			}
		}catch(Exception e) {
			log.error("majDebutClient : clientMdl {}",clientMdl,e);
		}
	}
	
	/**
	 * mise à jour dans l'arbre xml OSE de :
	 * - attribut stop
	 * - qtereel
	 * 
	 * @param clientMdl
	 * @param token
	 */
	public void majFinClient(ClientMdl clientMdl, String token) {
		try {
			GetSceResponseMdl getSceResponseMdl = getServiceOse(token);
			
			if(getSceResponseMdl!=null) {
				for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
					if(isMemeTournee(sceTourneeMdl,clientMdl)) {
						for(PcMdl pcMdl : sceTourneeMdl.getDessertes().getPc()) {
							if(clientMdl.getId().equals(pcMdl.getId())) {
								pcMdl.getExe().setStart(longToDateTimeFmt(clientMdl.getDebut()));
								pcMdl.getExe().setStop(longToDateTimeFmt(clientMdl.getFin()));
								
								for(OpMdl opMdl : pcMdl.getOperations().getOp()) {
									if(opMdl.getDispo()!=null && !opMdl.getDispo().isEmpty()) {
										for(DispoMdl dispo : opMdl.getDispo()) {
											dispo.setQtereel(getQtereelFromClient(clientMdl, opMdl, dispo));
											OperationMdl operationMdlFromClient = getOperationFromClient(clientMdl, opMdl, dispo);
											dispo.setQteabsent(operationMdlFromClient!=null?String.valueOf(operationMdlFromClient.getBacabsent()):"0");
											dispo.setQtevide(operationMdlFromClient!=null?String.valueOf(operationMdlFromClient.getBacvide()):"0");
										}
									}
								}
								
								if(ClientMdl.Presence.PRESENT.toString().equals(clientMdl.getPresence())) {
									pcMdl.setStatus(PcMdl.Status.CLOS.toString());
								}else {
									pcMdl.setStatus(clientMdl.getPresence());
								}
								
								ReceptMdl receptMdl = new ReceptMdl();
								ContactMdl recept = clientMdl.getRecept();
								if(recept!=null) {
									if(recept.getTitre()!=null) {
										receptMdl.setNomprenom(recept.getTitre());
									}
									if(recept.getTel()!=null) {
										receptMdl.setTel(recept.getTel());
									}
									if(recept.getFonction()!=null) {
										receptMdl.setFonction(recept.getFonction());
									}
									if(recept.getId()!=null) {
										receptMdl.setId(recept.getId());
									}
								}
								
								List<RessourceMediaMdl> listRessourceMedia = new ArrayList<>();
								
								if(clientMdl.getAudio()!=null) {
									if(clientMdl.getAudio().getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
										clientMdl.setAudio(mediaService.enregistreMedia(clientMdl.getAudio()));
									}
									receptMdl.setAudio(clientMdl.getAudio().getUrl());
									RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
									ressourceMediaMdl.setType(RessourceMediaMdl.Type.AUDIO.toString().toLowerCase());
									ressourceMediaMdl.setUrl(clientMdl.getAudio().getUrl());
									ressourceMediaMdl.setDateheure(longToDateTimeFmt(clientMdl.getAudio().getDateheure()));
									listRessourceMedia.add(ressourceMediaMdl);
								}
								
								if(clientMdl.getSignature()!=null) {
									if(clientMdl.getSignature().getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
										clientMdl.setSignature(mediaService.enregistreMedia(clientMdl.getSignature()));
									}
									receptMdl.setSignature(clientMdl.getSignature().getUrl());
									RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
									ressourceMediaMdl.setType(RessourceMediaMdl.Type.SIGNATURE.toString().toLowerCase());
									ressourceMediaMdl.setUrl(clientMdl.getSignature().getUrl());
									ressourceMediaMdl.setDateheure(longToDateTimeFmt(clientMdl.getSignature().getDateheure()));
									listRessourceMedia.add(ressourceMediaMdl);
								}
								
								pcMdl.getExe().setRecept(receptMdl);
								
								for(OpMdl opMdl : pcMdl.getOperations().getOp()) {
									if(opMdl!=null && opMdl.getCompo()!=null) {

										if(clientMdl.getSigltaudio()!=null && !clientMdl.getSigltaudio().isEmpty()) {
											for(MediaMdl mediaMdl : clientMdl.getSigltaudio()) {
												if(mediaMdl.getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
													mediaMdl = mediaService.enregistreMedia(mediaMdl);
												}
												RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
												ressourceMediaMdl.setType(RessourceMediaMdl.Type.SIGNALEMENT.toString().toLowerCase());
												ressourceMediaMdl.setId(mediaMdl.getIdsignalementose()!=null?mediaMdl.getIdsignalementose():Constantes.IDSIGNALEMENT_AUTRE);
												AudioMdl audioMdl = new AudioMdl();
												audioMdl.setUrl(mediaMdl.getUrl());
												audioMdl.setDateheure(longToDateTimeFmt(mediaMdl.getDateheure()));
												List<AudioMdl> audios = new ArrayList<>();
												audios.add(audioMdl);
												ressourceMediaMdl.setAudio(audios);
												ressourceMediaMdl.setCompo(opMdl.getCompo());
												listRessourceMedia.add(ressourceMediaMdl);
											}
										}
										
										if(clientMdl.getSigltimage()!=null && !clientMdl.getSigltimage().isEmpty()) {
											for(MediaMdl mediaMdl : clientMdl.getSigltimage()) {
												if(mediaMdl.getEtatsync().equals(MediaMdl.Etatsync.CLIENT.toString())) {
													mediaMdl = mediaService.enregistreMedia(mediaMdl);
												}
												RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
												ressourceMediaMdl.setType(RessourceMediaMdl.Type.SIGNALEMENT.toString().toLowerCase());
												ressourceMediaMdl.setId(mediaMdl.getIdsignalementose()!=null?mediaMdl.getIdsignalementose():Constantes.IDSIGNALEMENT_AUTRE);
												ImageMdl imageMdl = new ImageMdl();
												imageMdl.setUrl(mediaMdl.getUrl());
												imageMdl.setDateheure(longToDateTimeFmt(mediaMdl.getDateheure()));
												List<ImageMdl> images = new ArrayList<>();
												images.add(imageMdl);
												ressourceMediaMdl.setImage(images);
												ressourceMediaMdl.setCompo(opMdl.getCompo());
												
												listRessourceMedia.add(ressourceMediaMdl);
											}
										}
										
										// signalement texte
										if(!Strings.isBlank(clientMdl.getSiglttext())) {
											RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
											ressourceMediaMdl.setType(RessourceMediaMdl.Type.SIGNALEMENT.toString().toLowerCase());
											ressourceMediaMdl.setId(clientMdl.getSiglttextidsignalement()!=null?clientMdl.getSiglttextidsignalement():Constantes.IDSIGNALEMENT_AUTRE);
											CommentMdl commentMdl = new CommentMdl();
											commentMdl.setValue(clientMdl.getSiglttext());
											ressourceMediaMdl.setComment(commentMdl);
											ressourceMediaMdl.setCompo(opMdl.getCompo());
											listRessourceMedia.add(ressourceMediaMdl);
										}
									}
								}
								
								pcMdl.getExe().setRemontees(listRessourceMedia);
							}
						}
					}
				}
				majDeroulementTourneeMdl(token, null, getSceResponseMdl,Constantes.DRL_CTX_FIN_CLIENT);

			}else {
				log.error("majFinClient : getSceResponseMdl null pour token : {}",token);
			}
		}catch(Exception e) {
			log.error("majFinClient : {}",clientMdl,e);
		}
	}
		
	private String getPdsreelFromTournee(final Tournee tournee, final String clid, final OpMdl opMdl, final DispoMdl dispo) {
		String pdsreel = null;
		List<Collecte> collectes = collecteRepository.findByTournee(tournee);
		for(Collecte collecte : collectes) {
			Clientose clientose = collecte.getClientose();
			if(clientose!=null && clientose.getIdose().equals(clid)) {
				List<Prestation> prestations = prestationRepository.findByCollecte(collecte);
				for(Prestation prestation : prestations) {
					if(prestation.getCleose()!=null) {
						String cleOseOp = tourneeUtilService.genereCleOseOperation(opMdl,dispo);
						if(prestation.getCleose().equals(cleOseOp)) {
							pdsreel = String.valueOf(prestation.getPoids());
							break;
						}
					}else {
						if(prestation.getOpid().equals(opMdl.getId()) && prestation.getDispoid().equals(dispo.getId())) {
							pdsreel = String.valueOf(prestation.getPoids());
							break;
						}
					}
				}
			}
		}
		if(pdsreel==null) {
			log.warn("getPdsreelFromTournee : pdsreel null pour clientid : {}, operationId : {}, et tournee : {}",clid,opMdl.getId(),tournee);
			pdsreel = "?";
		}
		return pdsreel;
	}
	
	private OperationMdl getOperationFromClient(ClientMdl clientMdl, OpMdl opMdl, DispoMdl dispo) {
		OperationMdl operationMdlFromClient = null;
		for(OperationMdl operationMdl : clientMdl.getOperations()) {
			if(operationMdl.getCleose()!=null) {
				String cleOseOp = tourneeUtilService.genereCleOseOperation(opMdl,dispo);
				if(operationMdl.getCleose().equals(cleOseOp)) {
					operationMdlFromClient = operationMdl;
					break;
				}
			}else {
				String dispoid = (dispo!=null && dispo.getId()!=null)?dispo.getId():"0";
				if(operationMdl.getId().equals(opMdl.getId()) && operationMdl.getDispoid().equals(dispoid)) {
					operationMdlFromClient = operationMdl;
					break;
				}
			}
		}
		return operationMdlFromClient;
	}
	
	
	private String getQtereelFromClient(ClientMdl clientMdl, OpMdl opMdl, DispoMdl dispo) {
		String qtereel = null;
		for(OperationMdl operationMdl : clientMdl.getOperations()) {
			if(operationMdl.getCleose()!=null) {
				String cleOseOp = tourneeUtilService.genereCleOseOperation(opMdl,dispo);
				if(operationMdl.getCleose().equals(cleOseOp)) {
					qtereel = operationMdl.getQtereel();
					break;
				}
			}else {
				String dispoid = (dispo!=null && dispo.getId()!=null)?dispo.getId():"0";
				if(operationMdl.getId().equals(opMdl.getId()) && operationMdl.getDispoid().equals(dispoid)) {
					qtereel = operationMdl.getQtereel();
					break;
				}
			}
		}
		if(qtereel==null) {
			log.warn("getQtereelFromClient : qtereel null pour id : {} , et clientMdl : {}",opMdl.getId(),clientMdl);
			qtereel = "?";
		}
		return qtereel;
	}
	
	/**
	 * mise à jour dans l'arbre xml OSE de :
	 * - attribut start dans desserte
	 * 
	 * @param tourneeInfoMdl
	 * @param token
	 */
	public void majDebutTournee(TourneeInfoMdl tourneeInfoMdl, String token) {
		GetSceResponseMdl getSceResponseMdl = getServiceOse(token);
		
		for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
			if(isMemeTournee(sceTourneeMdl,tourneeInfoMdl)) {
				sceTourneeMdl.getDessertes().setStart(longToDateTimeFmt(tourneeInfoMdl.getDebut()));
			}
		}
		majDeroulementTourneeMdl(token, null, getSceResponseMdl,Constantes.DRL_CTX_DEBUT_TOURNEE);

		if(!isAdnaitest(getSceResponseMdl)) {
			putService(filtreGetSceResponseMdlParTour(getSceResponseMdl, tourneeInfoMdl.getTour()));
		}
	}
	
	private boolean isMemeTournee(final SceTourneeMdl sceTourneeMdl, final Tournee tournee) {
		boolean isMemeTournee = false;
		
		if(sceTourneeMdl.getId().equals(tournee.getIdose())) {
			isMemeTournee = true;
		}
		
		if(sceTourneeMdl.getTour()!=null && sceTourneeMdl.getTour().equals(String.valueOf(tournee.getNotour()))) {
			isMemeTournee = true;
		}else {
			isMemeTournee = false;
		}
		
		return isMemeTournee;
	}
	
	private boolean isMemeTournee(SceTourneeMdl sceTourneeMdl, TourneeInfoMdl tourneeInfoMdl) {
		boolean isMemeTournee = false;
		
		// TODO à revoir
		if(sceTourneeMdl.getId().equals(tourneeInfoMdl.getIdentifiant())) {
			isMemeTournee = true;
		}
		
		if(sceTourneeMdl.getTour()!=null && sceTourneeMdl.getTour().equals(tourneeInfoMdl.getTour())) {
			isMemeTournee = true;
		}else {
			isMemeTournee = false;
		}
		
		return isMemeTournee;
	}
	
	private boolean isMemeTournee(final SceTourneeMdl sceTourneeMdl, final Collecte collecte) {
		boolean isMemeTournee = false;
		
		Tournee tournee = collecte.getTournee();
		if(sceTourneeMdl.getId().equals(tournee.getIdose())) {
			isMemeTournee = true;
		}
		
		if(sceTourneeMdl.getTour()!=null && sceTourneeMdl.getTour().equals(String.valueOf(collecte.getNotourorigine()))) {
			isMemeTournee = true;
		}else {
			isMemeTournee = false;
		}
		
		return isMemeTournee;
	}
	
	private boolean isMemeTournee(SceTourneeMdl sceTourneeMdl, ClientMdl clientMdl) {
		boolean isMemeTournee = false;
		
		// TODO à revoir
		if(sceTourneeMdl.getId().equals(clientMdl.getTid())) {
			isMemeTournee = true;
		}
		
		if(sceTourneeMdl.getTour()!=null && sceTourneeMdl.getTour().equals(clientMdl.getTourorigine())) {
			isMemeTournee = true;
		}else {
			isMemeTournee = false;
		}
		
		return isMemeTournee;
	}
		
	private boolean isAdnaitest(GetSceResponseMdl getSceResponseMdl) {
		boolean isAdnaitest = false;
		for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
			if(sceTourneeMdl.getId().startsWith(Constantes.PREFIX_TOURNEE_ADNAITEST)) {
				isAdnaitest = true;
				break;
			}
		}
		return isAdnaitest;
	}
	
	public void putService(final GetSceResponseMdl getSceResponseMdl) {
		try {
			String endPoint = env.getProperty(Constantes.OSE_SERVICE_PUT);
			CloseableHttpClient httpClient = httpClientBuilder.build();
			HttpPost httpPost = new HttpPost(endPoint);
			
			StringWriter outputHtmlWriter = new StringWriter();
			Serializer serializer = new Persister();
			serializer.write(getSceResponseMdl, outputHtmlWriter);
			
			String reponse = "<reponse type=\"resultat\">" + outputHtmlWriter.toString() + "</reponse>";
			
			reponse = reponse.replace("<getSceResponseMdl>", "");
			reponse = reponse.replace("</getSceResponseMdl>", "");
			
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("xmldata", reponse));
			
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
			
		    HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			
			log.debug("putService : {}",getSceResponseMdl);
			log.debug("putService : xml {}",reponse);
			log.debug("putService : result {}",result);
			
			if(!isResultOk(result)) {
				log.error("putService : result KO : " + result + ", pour getSceResponseMdl : " + getSceResponseMdl);
			}
		}catch(Exception e) {
			log.error("putService : " + e.toString());
		}
	}
	
	/**
	 * Cas d’un POST OK
	 * <reponse type="valide" origine="put">Datas XML Chargees</reponse>
	 * Cas d’un POST pas OK
	 * <reponse type="erreur" origine="put">....motif...</reponse>
	 * 
	 * @param result
	 * @return
	 */
	private boolean isResultOk(String result) {
		boolean resultOk = false;
		try {
			if (result != null) {
				resultOk = result.contains("Datas XML Chargees");
			}
		}catch(Exception e) {
			log.error("isResultOk : " + e.toString() + ", result : " + result);
		}
		return resultOk;
	}
	
	public String getFluxOse(final String sid) {
		String fluxose = null;
		try {
			if(sid.startsWith(Constantes.PREFIX_TOURNEE_ADNAITEST)) {
				/*String urlmockdata = Constantes.S3_MOCKDATA_URL + sid + ".xml";
				try(BufferedInputStream in = new BufferedInputStream(new URL(urlmockdata).openStream())){
					fluxose = IOUtils.toString(in, StandardCharsets.UTF_8.name());
				}*/

				//GetObjectRequest getObjectRequest = new GetObjectRequest(Constantes.S3_BUCKET, "mockdata/" + sid + ".xml");
				//S3Object s3Object = amazonS3.getObject(getObjectRequest);
				fluxose = amazonS3.getObjectAsString(Constantes.S3_BUCKET, "mockdata/" + sid + ".xml");
			}else {
				String endPoint = env.getProperty(Constantes.OSE_SERVICE_GETSCE);
				log.debug("serviceInfo : endPoint : " + endPoint);
				
				CloseableHttpClient httpClient = httpClientBuilder.build();
				HttpPost httpPost = new HttpPost(endPoint);
				
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("daykey", sid));
				
				httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
								
			    HttpResponse response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				fluxose = EntityUtils.toString(entity);
			}
		}catch(Exception e) {
			log.error("getFluxOse {}",sid,e);
		}
		return fluxose;
	}
	
	public GetSceResponseMdl getSceResponse(String result) {
		GetSceResponseMdl getSceResponseMdl = null;
		try {
			if (result != null) {			
				Serializer serializer = new Persister();
				getSceResponseMdl = serializer.read(GetSceResponseMdl.class, result, false);
			}
		} catch (Exception e) {
			log.error("getSceResponse : " + e.toString() + " avec result : " + result);
		}
		return getSceResponseMdl;
	}
	
	public String stockeFluxOse(String xml, String codeservice) {
		String cleflux = null;
		try {
			byte[] data = xml.getBytes();			
			InputStream inputStream = new ByteArrayInputStream(data, 0, data.length);

			Date datedujour = new Date();
			
			String bucket = Constantes.S3_BUCKET;
			//String filename = longToDateTimeFmtPattern(datedujour.getTime(),"yyyy-MM-dd-HHmmss-SSS") + "-" 
			// + codeservice.toLowerCase()  + Constantes.FLUXOSE_EXT;
			String filename = longToDateTimeFmtPattern(datedujour.getTime(),"yyyy-MM-dd") + "-" + codeservice.toLowerCase() + "-" + longToDateTimeFmtPattern(datedujour.getTime(),"HHmmss-SSS") 
					 + Constantes.FLUXOSE_EXT;
	        String key = Constantes.S3_FLUXOSE_FOLDER + filename;
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(Constantes.FLUXOSE_CONTENT_TYPE);
	        metadata.setContentLength(data.length);
			
			PutObjectRequest pRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
			pRequest.setCannedAcl(CannedAccessControlList.PublicRead);
			
			amazonS3.putObject(pRequest);
			cleflux = key;
		}catch(Exception e) {
			log.error("stockeFluxOse : xml {}, codeservice {}",xml,codeservice,e);
		}
		return cleflux;
	}
	
	public String extraitVehicule(SceTourneeMdl sceTournee) {
		String vehicule = null;
		try {
			vehicule = sceTournee.getRessources().getVehicule().getNom();
		} catch (Exception e) {
			log.error("extraitVehicule : sceTournee {}",sceTournee,e);
		}
		return vehicule;
	}
	
	public AdresseMdl construitAdresse(LocMdl loc) {
		AdresseMdl adresseMdl = new AdresseMdl();
		
		for(AMdl a : loc.getA()) {
			switch(a.getNom()) {
				case "adr1":
					adresseMdl.setAdr1(a.getValue());
					break;
				case "adr2":
					adresseMdl.setAdr2(a.getValue());
					break;
				case "cp":
					adresseMdl.setCodepostal(a.getValue());
					break;
				case "ville":
					adresseMdl.setVille(a.getValue());
					break;
			}
		}
		String libadr = adresseMdl.getAdr1() + ",";
		if(adresseMdl.getAdr2()!=null && !adresseMdl.getAdr2().replace(".", "").trim().isEmpty()) {
			libadr = libadr + adresseMdl.getAdr2() + ",";
		}
		libadr = libadr + adresseMdl.getCodepostal() 
			+ " " + adresseMdl.getVille() + ", FR";
		adresseMdl.setLibadr(libadr);
		//String hashadr = Base64.getEncoder().encodeToString(libadr.getBytes());
		//adresseMdl.setHashadr(hashadr);
		
		// lat, lng
		adresseMdl.setLatitude(parseDouble(loc.getLat()));
		adresseMdl.setLongitude(parseDouble(loc.getLng()));
		
		return adresseMdl;
	}
	
	
	private CollecteDib findCollecteDibByIdose(final String idose, final List<CollecteDib> collectes) {
		CollecteDib collecteDibIdose = null;
		for(CollecteDib collecteDib : collectes) {
			if(collecteDib.getClientose().getIdose().equals(idose)) {
				collecteDibIdose = collecteDib;
				break;
			}
		}
		
		return collecteDibIdose;
	}
	
	private PrestationDib findPrestationDib(final OpMdl opMdl, final DispoMdl dispo, List<PrestationDib> prestations) {
		PrestationDib prestationDibSelect = null;
		String cleose = tourneeUtilService.genereCleOseOperation(opMdl,dispo);
		for(PrestationDib prestationDib : prestations) {
			if(cleose.equals(prestationDib.getCleose())) {
				prestationDibSelect = prestationDib;
				break;
			}
		}
		
		return prestationDibSelect;
	}
	
	private String transcodeEtatPc(final String etatCollecte) {
		String etatPc = "att";
		if(CollecteDib.Etat.TRAITE.toString().equals(etatCollecte)) {
			etatPc = PcMdl.Status.CLOS.toString();
		}else if(CollecteDib.Etat.IMPOSSIBLE.toString().equals(etatCollecte)) {
			etatPc = PcMdl.Status.FERME.toString();
		}
		return etatPc;
	}
	
	private String calculIdSignalement(final String motif, final int idmandant) {
		String idSignalement = Constantes.IDSIGNALEMENT_AUTRE; // Autre
		if(StringUtils.isNumeric(motif)) {
			idSignalement = motif;
		}else {
			TranscoSignalement transcoSignalement = transcoSignalementRepository.findFirstByMotifadnaiAndIdmandant(motif, idmandant);
			if(transcoSignalement!=null) {
				idSignalement = transcoSignalement.getIdcedre();
			}
		}
		return idSignalement;
	}
	
	
	public boolean majTourneeOse(final MajTourneeOseMdl majTourneeOseMdl) {
		boolean retour = false;
		try {
			log.debug("majTourneeOse {}",majTourneeOseMdl);
			Tournee tournee = tourneeRepository.findById(majTourneeOseMdl.getId()).get();
			if(Tournee.Typetournee.DIB.toString().equals(tournee.getTypetournee())) {
				retour = majTourneeDibOse(tournee);
			}else if(Tournee.Typetournee.COLLECTE.toString().equals(tournee.getTypetournee())) {
				retour = majTourneeCollecteOse(tournee);
			}else {
				log.error("majTourneeOse unsupported typetournee {}",tournee.getTypetournee());
			}
		}catch (Exception e) {
			log.error("majTourneeOse : majTourneeOseMdl {}",majTourneeOseMdl,e);
		}
		return retour;
	}
	
	public boolean majTourneeCollecteOse(final Tournee tournee) {
		boolean retour = false;
		try {
			GetSceResponseMdl getSceResponseMdl = getSceResponseFromS3(tournee.getCleflux());

			if(getSceResponseMdl!=null) {
				if(tournee.isFusion()) {
					for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
						sceTourneeMdl.getDessertes().setStop(dateTimeFmt(tournee.getDhfin()));
						sceTourneeMdl.getDessertes().setStart(dateTimeFmt(tournee.getDhdebut()));
					}
					
					List<Collecte> collectes = collecteRepository.findByTournee(tournee);

					for(Collecte collecte : collectes) {
						for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
							if(isMemeTournee(sceTourneeMdl,collecte)) {
								if(sceTourneeMdl.getDessertes().getPc()!=null) {
									for(PcMdl pcMdl : sceTourneeMdl.getDessertes().getPc()) {
										Clientose clientose = collecte.getClientose();
										if(clientose!=null && clientose.getIdose().equals(pcMdl.getId())) {
											for(OpMdl opMdl : pcMdl.getOperations().getOp()) {
												if(opMdl.getDispo()!=null && !opMdl.getDispo().isEmpty()) {
													double pdscompo = 0;
													for(DispoMdl dispo : opMdl.getDispo()) {
														String pdsreel = getPdsreelFromTournee(tournee, pcMdl.getId(), opMdl, dispo);
														dispo.setPdsreel(pdsreel);
														pdscompo = pdscompo + parsePoids(traitementPdsOse(pdsreel));
													}
													opMdl.setPdscompo(String.valueOf(pdscompo));
												}
											}
											if(collecte.getUrlavp()!=null) {
												RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
												ressourceMediaMdl.setType(RessourceMediaMdl.Type.AVISDEPASSAGE.toString().toLowerCase());
												ressourceMediaMdl.setUrl(collecte.getUrlavp());
												pcMdl.getExe().getRemontees().add(ressourceMediaMdl);

												List<Media> mediasAvp = mediaRepository.findByCollecteAndTypemedia(collecte, Media.Typemedia.AVPIMAGE.toString());
												for(Media mediaAvp : mediasAvp){
													RessourceMediaMdl ressourceMediaAvpMdl = new RessourceMediaMdl();
													ressourceMediaAvpMdl.setType(RessourceMediaMdl.Type.AVPIMAGE.toString().toLowerCase());
													ressourceMediaAvpMdl.setUrl(mediaAvp.getUrl());
													ressourceMediaAvpMdl.setDateheure(dateTimeFmt(mediaAvp.getDatecreation()));
													pcMdl.getExe().getRemontees().add(ressourceMediaAvpMdl);
												}
											}
										}
									}
								}
							}
						}
					}
				}else {
					for(SceTourneeMdl sceTourneeMdl : getSceResponseMdl.getSce()) {
						if(isMemeTournee(sceTourneeMdl,tournee)) {
							sceTourneeMdl.getDessertes().setStop(dateTimeFmt(tournee.getDhfin()));
							if(sceTourneeMdl.getDessertes().getPc()!=null) {
								for(PcMdl pcMdl : sceTourneeMdl.getDessertes().getPc()) {
									for(OpMdl opMdl : pcMdl.getOperations().getOp()) {
										if(opMdl.getDispo()!=null && !opMdl.getDispo().isEmpty()) {
											double pdscompo = 0;
											for(DispoMdl dispo : opMdl.getDispo()) {
												String pdsreel = getPdsreelFromTournee(tournee, pcMdl.getId(), opMdl, dispo);
												dispo.setPdsreel(pdsreel);
												pdscompo = pdscompo + parsePoids(traitementPdsOse(pdsreel));
											}
											opMdl.setPdscompo(String.valueOf(pdscompo));
										}
									}
									List<Collecte> collectes = collecteRepository.findByTournee(tournee);
									for(Collecte collecte : collectes) {
										Clientose clientose = collecte.getClientose();
										if(clientose.getIdose().equals(pcMdl.getId()) && collecte.getUrlavp()!=null) {
											RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
											ressourceMediaMdl.setType(RessourceMediaMdl.Type.AVISDEPASSAGE.toString().toLowerCase());
											ressourceMediaMdl.setUrl(collecte.getUrlavp());
											pcMdl.getExe().getRemontees().add(ressourceMediaMdl);

											List<Media> mediasAvp = mediaRepository.findByCollecteAndTypemedia(collecte, Media.Typemedia.AVPIMAGE.toString());
											for(Media mediaAvp : mediasAvp){
												RessourceMediaMdl ressourceMediaAvpMdl = new RessourceMediaMdl();
												ressourceMediaAvpMdl.setType(RessourceMediaMdl.Type.AVPIMAGE.toString().toLowerCase());
												ressourceMediaAvpMdl.setUrl(mediaAvp.getUrl());
												ressourceMediaAvpMdl.setDateheure(dateTimeFmt(mediaAvp.getDatecreation()));
												pcMdl.getExe().getRemontees().add(ressourceMediaAvpMdl);
											}
										}
									}
								}
							}
						}
					}
				}

				//deroulementService.majDeroulementTourneeMdl(token, tourneeInfoMdl, getSceResponseMdl,Constantes.DRL_CTX_FIN_TOURNEE);
				// stockage nouvelle version du flux
				StringWriter outputWriter = new StringWriter();
				Serializer serializer = new Persister();
				serializer.write(getSceResponseMdl, outputWriter);
				String xml = outputWriter.toString();
				
				String cles3 = stockeFluxOse(xml ,tournee.getCodeservice());
				log.debug("majTourneeCollecteOse : cles3 {}",cles3);
				
				if(!isAdnaitest(getSceResponseMdl)) {
					historiqueCodeOseService.addHistoriqueCodeOse(tournee.getCodeservice(), String.valueOf(tournee.getNotour()), tournee.getIdose());
					putService(filtreGetSceResponseMdlParTour(getSceResponseMdl, String.valueOf(tournee.getNotour())));
				}
				
				tournee.setDhmajose(LocalDateTime.now());
				tourneeRepository.save(tournee);
				
				retour = true;
			}else {
				log.error("majTourneeCollecteOse : getSceResponseMdl null pour tounee.id : {}",tournee.getId());
			}
		}catch (Exception e) {
			log.error("majTourneeCollecteOse : tournee {}",tournee,e);
		}
		return retour;
	}
	
	public boolean majTourneeDibOse(final Tournee tournee) {
		boolean retour = false;
		try {
			GetSceResponseMdl getSceResponseMdl = getSceResponseFromS3(tournee.getCleflux());
			
			List<CollecteDib> collectes = collecteDibRepository.findByTournee(tournee);

			if(getSceResponseMdl!=null && getSceResponseMdl.getSce()!=null && !getSceResponseMdl.getSce().isEmpty()) {
				SceTourneeMdl sceTournee = getSceResponseMdl.getSce().get(0);
				
				sceTournee.setPdstotal(tournee.getPoidsdib()!=null?String.valueOf(tournee.getPoidsdib()):"");
				
				sceTournee.getDessertes().setStart(dateTimeFmt(tournee.getDhdebut()));
				sceTournee.getDessertes().setStop(dateTimeFmt(tournee.getDhfin()));
				
				for(PcMdl pcMdl : sceTournee.getDessertes().getPc()) {
					CollecteDib collecteDib = findCollecteDibByIdose(pcMdl.getId(), collectes);
					if(collecteDib!=null) {
						List<PrestationDib> prestations = prestationDibRepository.findByCollectedib(collecteDib);
						pcMdl.getExe().setStart(toLocalDateTimeFmt(collecteDib.getDhpassage()));
						pcMdl.getExe().setStop(toLocalDateTimeFmt(collecteDib.getDhpassage()));
						
						pcMdl.setStatus(transcodeEtatPc(collecteDib.getEtat()));
						
						// remontées
						List<MediaDib> medias = mediaDibRepository.findByIdcollectedib(collecteDib.getId());
						List<RessourceMediaMdl> remontees = new ArrayList<>();
						for(MediaDib mediaDib : medias) {
							if(mediaDib.getIdprestationdib()>0) {
								RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
								ressourceMediaMdl.setType(RessourceMediaMdl.Type.SIGNALEMENT.toString().toLowerCase());
								ressourceMediaMdl.setId(calculIdSignalement(mediaDib.getMotif(),collecteDib.getMandant()));
								ImageMdl imageMdl = new ImageMdl();
								imageMdl.setUrl(mediaDib.getUrl());
								imageMdl.setDateheure(toLocalDateTimeFmt(mediaDib.getDhcreation()));
								List<ImageMdl> images = new ArrayList<>();
								images.add(imageMdl);
								ressourceMediaMdl.setImage(images);
								
								//if(mediaDib.getIdprestationdib()>0) {
								Optional<PrestationDib> optPrestationDib = prestationDibRepository.findById(mediaDib.getIdprestationdib());
								if(optPrestationDib.isPresent()) {
									ressourceMediaMdl.setCompo(optPrestationDib.get().getCompo());
								}
								//}
								remontees.add(ressourceMediaMdl);
							}else {
								for(PrestationDib prestationDib : prestations) {
									RessourceMediaMdl ressourceMediaMdl = new RessourceMediaMdl();
									ressourceMediaMdl.setType(RessourceMediaMdl.Type.SIGNALEMENT.toString().toLowerCase());
									ressourceMediaMdl.setId(calculIdSignalement(mediaDib.getMotif(),collecteDib.getMandant()));
									ImageMdl imageMdl = new ImageMdl();
									imageMdl.setUrl(mediaDib.getUrl());
									imageMdl.setDateheure(toLocalDateTimeFmt(mediaDib.getDhcreation()));
									List<ImageMdl> images = new ArrayList<>();
									images.add(imageMdl);
									ressourceMediaMdl.setImage(images);
									ressourceMediaMdl.setCompo(prestationDib.getCompo());
									remontees.add(ressourceMediaMdl);
								}
							}
						}
						pcMdl.getExe().setRemontees(remontees);
						
						// operations
						for(OpMdl opMdl : pcMdl.getOperations().getOp()) {
							if(opMdl.getDispo()!=null && !opMdl.getDispo().isEmpty()) {
								for(DispoMdl dispo : opMdl.getDispo()) {
									PrestationDib prestationDib = findPrestationDib(opMdl, dispo, prestations);
									dispo.setQtereel(String.valueOf(prestationDib.getQtereel()));
									dispo.setQtevide(String.valueOf(prestationDib.getQtevide()));
									dispo.setQteabsent(String.valueOf(prestationDib.getQteabsent()));
									dispo.setQtedebord(String.valueOf(prestationDib.getQtedebord()));
									dispo.setQtedeclassement(String.valueOf(prestationDib.getQtedeclassement()));
								}
							}
						}
					}
				}

				// stockage nouvelle version du flux
				StringWriter outputWriter = new StringWriter();
				Serializer serializer = new Persister();
				serializer.write(getSceResponseMdl, outputWriter);
				String xml = outputWriter.toString();
				
				String cles3 = stockeFluxOse(xml ,tournee.getCodeservice());
				log.debug("majTourneeDibOse : cles3 {}",cles3);
				
				// put service
				if(!isAdnaitest(getSceResponseMdl)) {
					putService(getSceResponseMdl);
				}
				
				tournee.setDhmajose(LocalDateTime.now());
				tourneeRepository.save(tournee);
				
				retour = true;

			}
		}catch (Exception e) {
			log.error("majTourneeDibOse : tournee {}",tournee,e);
		}
		return retour;
	}
	
	public GetSceResponseMdl getSceResponseFromS3(final String s3key) {
		GetSceResponseMdl getSceResponseMdl = null;
		try {
			S3Object s3Object = amazonS3.getObject(Constantes.S3_BUCKET, s3key);
			S3ObjectInputStream s3is = s3Object.getObjectContent();

			String xml = IOUtils.toString(s3is, StandardCharsets.UTF_8.name());			
			s3is.close();

			if (xml != null) {			
				Serializer serializer = new Persister();
				getSceResponseMdl = serializer.read(GetSceResponseMdl.class, xml, false);
			}
		} catch (Exception e) {
			log.error("getSceResponseFromS3 : s3key {}",s3key,e);
		}
		return getSceResponseMdl;
	}

	public GetSceResponseMdl getServiceOse(final String token) {
		GetSceResponseMdl getSceResponseMdl = null;
		try {
			DeroulementTourneeMdl deroulementTourneeMdl = getDeroulementTourneeMdl(token);
			if(deroulementTourneeMdl!=null) {
				getSceResponseMdl = getSceResponseFromS3(deroulementTourneeMdl.getCleServiceOse());
			}
		} catch (Exception e) {
			log.error("getServiceOse : {}",token,e);
		}
		return getSceResponseMdl;
	}

	public DeroulementTourneeMdl getDeroulementTourneeMdl(final String token) {
		DeroulementTourneeMdl deroulementTourneeMdl = null;
		try {
			deroulementTourneeMdl = this.mapper.load(DeroulementTourneeMdl.class, token);
		}catch(Exception e) {
			log.error("getDeroulementTourneeMdl : {}",token,e);
		}
		return deroulementTourneeMdl;
	}

	public void majDeroulementTourneeMdl(final String token, final TourneeInfoMdl tourneeInfo, final GetSceResponseMdl getSceResponseMdl, final String contexte) {
		try {
			DeroulementTourneeMdl deroulementTourneeMdl = getDeroulementTourneeMdl(token);
			if(deroulementTourneeMdl!=null) {
				if(tourneeInfo!=null) {
					deroulementTourneeMdl.setTourneeMdl(tourneeInfo);
				}
				if(getSceResponseMdl!=null) {
					StringWriter outputWriter = new StringWriter();
					Serializer serializer = new Persister();
					serializer.write(getSceResponseMdl, outputWriter);
					String xml = outputWriter.toString();

					String clefluxose = stockeFluxOse(xml, deroulementTourneeMdl.getCodeservice());
					deroulementTourneeMdl.setCleServiceOse(clefluxose);
				}
				if(contexte!=null) {
					deroulementTourneeMdl.setContexte(contexte);
				}
				deroulementTourneeMdl.setLastmaj(commonUtilService.calculLastmaj());
				mapper.save(deroulementTourneeMdl);
			}
		}catch(Exception e) {
			log.error("majTournee : {},{}",token,tourneeInfo,e);
		}
	}
}
