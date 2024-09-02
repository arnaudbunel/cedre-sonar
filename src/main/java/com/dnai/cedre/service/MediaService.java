package com.dnai.cedre.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dnai.cedre.dao.CollecteRepository;
import com.dnai.cedre.dao.MediaDibRepository;
import com.dnai.cedre.dao.MediaRepository;
import com.dnai.cedre.domain.Collecte;
import com.dnai.cedre.domain.Media;
import com.dnai.cedre.domain.MediaDib;
import com.dnai.cedre.model.MediaMdl;
import com.dnai.cedre.model.MediaPropertyMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediaService {

	@Autowired
	private MediaRepository mediaRepository;
	
	@Autowired
	private MediaDibRepository mediaDibRepository;
	
	@Autowired
	private CollecteRepository collecteRepository;
		
	@Transactional
	public MediaMdl enregistreMedia(MediaMdl mediaMdl) {
		try {
			Collecte collecte = collecteRepository.findById(mediaMdl.getIdcollecte()).get();
			String base64 = mediaMdl.getBase64media();
			var base64Part = base64.split(",");
			if(base64Part.length<2) {
				log.warn("enregistreMedia : base64Part.length<2, base64 {}, mediaMdl {}",StringUtils.leftPad(mediaMdl.getBase64media(),50),mediaMdl);
				return mediaMdl;
			}
			String dataCt = base64Part[0];
			MediaPropertyMdl mediaPropertyMdl = calculMediaProperty(mediaMdl.getTypemedia(),dataCt);
			String bucket = Constantes.S3_BUCKET;
			String filename = mediaPropertyMdl.getFilenameprefix() + System.currentTimeMillis() + mediaPropertyMdl.getFilenameext();
	        String key = mediaPropertyMdl.getFolder() + filename;
	        
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Constantes.S3_REGION).build();

			Decoder decoder = Base64.getDecoder();
			byte[] data = decoder.decode(base64Part[1]);
			
			InputStream inputStream = new ByteArrayInputStream(data, 0, data.length);
			
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(mediaPropertyMdl.getContenttype());
	        metadata.setContentLength(data.length);
	        Map<String,String> userMetadata = new HashMap<>();
	        userMetadata.put(Constantes.S3_USRMTDT_IDTOURNEE, mediaMdl.getIdtournee());
	        userMetadata.put(Constantes.S3_USRMTDT_IDCLIENT, mediaMdl.getIdclient());
	        metadata.setUserMetadata(userMetadata);
			
			PutObjectRequest pRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
			pRequest.setCannedAcl(CannedAccessControlList.PublicRead);
	        
			String urlMedia = mediaPropertyMdl.getDebuturl() + filename;
			
			s3.putObject(pRequest);
							        
			mediaMdl.setEtatsync(MediaMdl.Etatsync.SERVER.toString());
			mediaMdl.setFichier(filename);
			mediaMdl.setUrl(urlMedia);
			
	        Media mediaNew = new Media();
	        mediaNew.setCles3(key);
	        mediaNew.setDatecreation(new Date(mediaMdl.getDateheure()));
	        mediaNew.setIdclient(mediaMdl.getIdclient());
	        mediaNew.setIdtournee(mediaMdl.getIdtournee());
	        mediaNew.setTypemedia(mediaMdl.getTypemedia());
	        mediaNew.setUrl(urlMedia);
	        mediaNew.setCollecte(collecte);
			
	        Media media = mediaRepository.save(mediaNew);
			mediaMdl.setBase64media(null);
			mediaMdl.setId(media.getId());
		}catch(Exception e) {
			log.error("enregistreMedia : mediaMdl {}, base64media {}",mediaMdl, StringUtils.leftPad(mediaMdl.getBase64media(),50),e);
		}
		return mediaMdl;
	}
	
	@Transactional
	public MediaMdl enregistreMediaDib(MediaMdl mediaMdl) {
		try {
			String base64 = mediaMdl.getBase64media();
			var base64Part = base64.split(",");
			if(base64Part.length<2) {
				log.warn("enregistreMediaDib : base64Part.length<2, base64 {}, mediaMdl {}",StringUtils.leftPad(mediaMdl.getBase64media(),50),mediaMdl);
				return mediaMdl;
			}
			String dataCt = base64Part[0];
			MediaPropertyMdl mediaPropertyMdl = calculMediaProperty(mediaMdl.getTypemedia(),dataCt);
			String bucket = Constantes.S3_BUCKET;
			String filename = mediaPropertyMdl.getFilenameprefix() + mediaMdl.getIdcollecte() + System.currentTimeMillis() + mediaPropertyMdl.getFilenameext();
	        String key = mediaPropertyMdl.getFolder() + filename;
	        
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Constantes.S3_REGION).build();

			Decoder decoder = Base64.getDecoder();
			byte[] data = decoder.decode(base64Part[1]);
			
			InputStream inputStream = new ByteArrayInputStream(data, 0, data.length);
			
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(mediaPropertyMdl.getContenttype());
	        metadata.setContentLength(data.length);
	        Map<String,String> userMetadata = new HashMap<>();
	        userMetadata.put(Constantes.S3_USRMTDT_IDTOURNEE, mediaMdl.getIdtournee());
	        userMetadata.put(Constantes.S3_USRMTDT_IDCLIENT, mediaMdl.getIdclient());
	        metadata.setUserMetadata(userMetadata);
			
			PutObjectRequest pRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
			pRequest.setCannedAcl(CannedAccessControlList.PublicRead);
	        
			String urlMedia = mediaPropertyMdl.getDebuturl() + filename;
			
			s3.putObject(pRequest);
							        
			mediaMdl.setEtatsync(MediaMdl.Etatsync.SERVER.toString());
			mediaMdl.setFichier(filename);
			mediaMdl.setUrl(urlMedia);
			
			MediaDib mediaNew = new MediaDib();
	        mediaNew.setCles3(key);
	        LocalDateTime dhcreation =
	        	       LocalDateTime.ofInstant(Instant.ofEpochMilli(mediaMdl.getDateheure()),
	        	                               TimeZone.getDefault().toZoneId());
	        mediaNew.setDhcreation(dhcreation);
	        mediaNew.setTypemedia(mediaMdl.getTypemedia());
	        mediaNew.setUrl(urlMedia);
	        mediaNew.setIdcollectedib(mediaMdl.getIdcollecte());
	        mediaNew.setIdprestationdib(mediaMdl.getIdprestation());
	        mediaNew.setLongitude(mediaMdl.getLongitude());
	        mediaNew.setLatitude(mediaMdl.getLatitude());
	        mediaNew.setMotif(mediaMdl.getMotif());
			
	        MediaDib media = mediaDibRepository.save(mediaNew);
			mediaMdl.setBase64media(null);
			mediaMdl.setId(media.getId());
			log.debug("enregistreMediaDib out : mediaMdl {}",mediaMdl);
		}catch(Exception e) {
			log.error("enregistreMediaDib : mediaMdl {}",mediaMdl,e);
		}
		return mediaMdl;
	}
	
	private MediaPropertyMdl calculMediaProperty(String typemedia, String data) {
		MediaPropertyMdl mediaPropertyMdl = new MediaPropertyMdl();
		
		switch(typemedia) {
			case "SIGNALEMENTIMAGE":
			case "SIGNATURE":
			case "AVPIMAGE":
				String contenttype = extraitContentType(data);
				if(contenttype==null) {
					contenttype = Constantes.IMAGE_PNG_CONTENT_TYPE;
				}
				mediaPropertyMdl.setContenttype(contenttype);
				mediaPropertyMdl.setFilenameext(calculExtension(contenttype));
				mediaPropertyMdl.setFilenameprefix(Constantes.S3_IMAGE_PREFIX);
				mediaPropertyMdl.setFolder(Constantes.S3_IMAGE_FOLDER);
				mediaPropertyMdl.setDebuturl(Constantes.S3_IMAGE_URL);
				break;
			case "SIGNALEMENTAUDIO":
			case "INFOCLIENT":
				mediaPropertyMdl.setContenttype(Constantes.AUDIO_WAV_CONTENT_TYPE);
				mediaPropertyMdl.setFilenameext(Constantes.AUDIO_WAV_EXT);
				mediaPropertyMdl.setFilenameprefix(Constantes.S3_AUDIO_PREFIX);
				mediaPropertyMdl.setFolder(Constantes.S3_AUDIO_FOLDER);
				mediaPropertyMdl.setDebuturl(Constantes.S3_AUDIO_URL);
				break;
			default:
				log.error("calculMediaProperty : unsupported typemedia {}",typemedia);
		}
		
		return mediaPropertyMdl;
	}
	
	private String extraitContentType(String data) {
		// data:image/png;base64
		String contentType = null;
		if(data!=null && data.length()>0) {
			int i = data.indexOf(":");
			int j = data.indexOf(";");
			if(i>-1 && j>-1 && j>i) {
				contentType = data.substring(i+1, j);
			}
		}
		return contentType;
	}
	
	private String calculExtension(String contentType) {
		return "." + contentType.split("/")[1];
	}

	@Transactional
	public void supprimeMedia(MediaMdl mediaMdl) {
		try {
			// TODO améliorer en passant l'id dans MediaResult
			Media media = mediaRepository.findFirstByUrl(mediaMdl.getUrl());
			if(media!=null) {
				String key = media.getCles3();
				AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Constantes.S3_REGION).build();
				DeleteObjectRequest dRequest = new DeleteObjectRequest(Constantes.S3_BUCKET,key);
				s3.deleteObject(dRequest);
				mediaRepository.delete(media);
			}else {
				log.error("supprimeMedia : media null pour mediaMdl : " + mediaMdl);
			}
		}catch (Exception e) {
			log.error("supprimeMedia : " + e.toString() + " avec mediaMdl : " + mediaMdl);
		}
	}
	
	@Transactional
	public void supprimeMediaDib(MediaMdl mediaMdl) {
		try {
			MediaDib media = mediaDibRepository.findById(mediaMdl.getId()).get();
			String key = media.getCles3();
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Constantes.S3_REGION).build();
			DeleteObjectRequest dRequest = new DeleteObjectRequest(Constantes.S3_BUCKET,key);
			s3.deleteObject(dRequest);
			mediaDibRepository.delete(media);
		}catch (Exception e) {
			log.error("supprimeMediaDib : mediaMdl : ",mediaMdl,e);
		}
	}
	
}
