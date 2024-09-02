package com.dnai.cedre.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dnai.cedre.model.cockpit.AvpMdl;
import com.dnai.cedre.util.Constantes;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AvisdePassageService /*extends ParentService*/{

	@Autowired
	private Environment env;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	public String genereEtStockeAvp(AvpMdl avpMdl) {
		String urlAvp = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileOutputStream outputStream = null;
		try {
			FopFactory fopFactory = FopFactory.newInstance(new File(getFopConf()));
			
		    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

		    TransformerFactory factory = TransformerFactory.newInstance();
		    // factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); // pose un PB
		    Transformer transformer = factory.newTransformer(new StreamSource(getFopXsl()));

		    String signatureUrlHttp = avpMdl.getSignature();
		    String signaturePath = null;
		    if(avpMdl.isSignaturePresente()) {
			    signaturePath = writeSignatureFile(signatureUrlHttp);
			    log.debug("signaturePath : " + signaturePath);
			    
			    String signature = "url(file:/";
			    if(signaturePath!=null && signaturePath.startsWith("/")) {
			    	signature += "/";
			    }
			    signature += signaturePath +")";
			    
			    avpMdl.setSignature(signature);
		    }
		    
		    ByteArrayInputStream bais = new ByteArrayInputStream(avpToXml(avpMdl));
		    Source src = new StreamSource(bais);

		    Result res = new SAXResult(fop.getDefaultHandler());

		    transformer.transform(src, res);
		    
		    if(avpMdl.isSignaturePresente()) {
			    Path pathSignature = Paths.get(signaturePath);
			    File fileSignature = pathSignature.toFile();
			    if(fileSignature.exists()) {
			    	boolean deleteok = fileSignature.delete();
			    	if(!deleteok) {
			    		log.error("genereEtStockeAvp : delete ko for {}",signaturePath);
			    	}
			    }
		    }
		    
		    // stockage dans s3
		    //if(!isTestProfile()) {
		    // TODO à revoir pour les tests
			//AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Constantes.S3_REGION).build();
			
			byte[] data = out.toByteArray();
			
			InputStream inputStream = new ByteArrayInputStream(data, 0, data.length);
			
			String bucket = Constantes.S3_BUCKET;
			String filename = Constantes.S3_AVP_PREFIX + avpMdl.getIdavp() + Constantes.AVP_EXT;
	        String key = Constantes.S3_AVP_FOLDER + filename;
			ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType(Constantes.AVP_CONTENT_TYPE);
	        metadata.setContentLength(data.length);
	        Map<String,String> userMetadata = new HashMap<>();
	        userMetadata.put(Constantes.S3_USRMTDT_IDTOURNEE, avpMdl.getIdtourneeose());
	        userMetadata.put(Constantes.S3_USRMTDT_IDCLIENT, avpMdl.getIdclientose());
	        metadata.setUserMetadata(userMetadata);
			
			PutObjectRequest pRequest = new PutObjectRequest(bucket, key, inputStream, metadata);
			pRequest.setCannedAcl(CannedAccessControlList.PublicRead);
	        
			urlAvp = Constantes.S3_AVP_URL + filename;
			
			amazonS3.putObject(pRequest);

		    out.close();
		}catch(Exception e) {
				log.error("genereEtStockeAvp : avpMdl : {}",avpMdl,e);
		}finally {
			if(outputStream!=null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("genereEtStockeAvp",e);
				}
			}
		}
		return urlAvp;
	}
	
	private String writeSignatureFile(String urlSignature) {
		String pathSignature = null;
		ReadableByteChannel readableByteChannel = null;
		try {
			Path path = Paths.get(env.getProperty(Constantes.DIRECTORY_SIGNATURE) + "/" + UUID.randomUUID().toString() + ".png");
			pathSignature = path.toString();
			URL url = new URL(urlSignature);
			readableByteChannel = Channels.newChannel(url.openStream());
			try(FileOutputStream fileOutputStream = new FileOutputStream(pathSignature)){
				fileOutputStream.getChannel()
				  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			}
		}catch(Exception e) {
			log.error("writeSignatureFile : urlSignature : {}",urlSignature,e);
		}finally {
			try {
				if(readableByteChannel!=null) {
						readableByteChannel.close();
				}
			} catch (IOException e) {
				log.error("writeSignatureFile",e);
			}
		}
		return pathSignature;
	}
	
	private byte[] avpToXml(AvpMdl avpMdl) {
		byte[] xmlStream = null;
	    try {
			Serializer serializer = new Persister();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
			serializer.write(avpMdl, baos);
			xmlStream = baos.toByteArray();
		} catch (Exception e) {
			log.error("avpToXml : " + e.toString());
		}
		return xmlStream;
	}
		
	private String getFopConf() throws URISyntaxException {
		Path path = Paths.get(getClass().getClassLoader()
			      .getResource("fop/fop.xconf").toURI());

		return path.toString();
	}
		
	private String getFopXsl() throws URISyntaxException {
		Path path = Paths.get(getClass().getClassLoader()
			      .getResource("fop/avp.xsl").toURI());

		return path.toString();
	}
}
