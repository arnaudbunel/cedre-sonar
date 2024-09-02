package com.dnai.cedre.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dnai.cedre.model.PositionMdl;
import com.dnai.cedre.util.Constantes;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.AddressType;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.GeocodingResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeolocalisationService /*extends ParentService*/{
	@Autowired
	private CommonUtilService commonUtilService;
	/**
	 * geocode adresse
	 * 
	 * @param adresse
	 * @return
	 */
	public PositionMdl geocodeAdresse(String adresse){
		GeoApiContext context = new GeoApiContext.Builder()
			    .apiKey(Constantes.GOOGLE_GEOAPI_KEY)
			    .build();
		
		GeocodingApiRequest request = GeocodingApi.geocode(context, adresse);
		request = request.region("fr");
		request = request.resultType(AddressType.STREET_ADDRESS);
		
		PositionMdl positionMdl = new PositionMdl();
		
		try {
			GeocodingResult[] result = request.await();
			
			List<GeocodingResult> listGrAvecCp = new ArrayList<>();
			
			if(result.length>1) {
				for(GeocodingResult gr : result) {
					if(calculCodePostal(gr)!=null) {
						listGrAvecCp.add(gr);
					}
				}
			}else if(result.length==1) {
				GeocodingResult gr = result[0];
				listGrAvecCp.add(gr);
			}
			
			if(listGrAvecCp.size()>0){
				GeocodingResult gr = listGrAvecCp.get(0);
				positionMdl.setLatitude(gr.geometry.location.lat);
				positionMdl.setLongitude(gr.geometry.location.lng);
				positionMdl.setTrouve(true);
				positionMdl.setAdresse(gr.formattedAddress);
				if(listGrAvecCp.size()>1) {
					log.error("geocodeAdresse : plusieurs résultats pour adresse : " + adresse);
				}
			}else{
				log.error("pb geocoding pour adresse : " + adresse);
				positionMdl.setTrouve(false);
			}
		} catch (Exception e) {
			log.error("geocodeAdresse : " + e.toString() + " pour adresse : " + adresse);
			positionMdl.setTrouve(false);
		}
		
		return positionMdl;
	}
	
	
	/**
	 * geocode adresse
	 * 
	 * @param adresse
	 * @return
	 */
	public PositionMdl geocodeAdresseStrict(String adresse){
		GeoApiContext context = new GeoApiContext.Builder()
			    .apiKey(Constantes.GOOGLE_GEOAPI_KEY)
			    .build();
		
		GeocodingApiRequest request = GeocodingApi.geocode(context, adresse);
		request = request.region("fr");
		request = request.resultType(AddressType.STREET_ADDRESS);
		
		PositionMdl positionMdl = new PositionMdl();
		
		try {
			GeocodingResult[] result = request.await();
			
			List<GeocodingResult> listGrAvecCp = new ArrayList<>();
			
			if(result.length>1) {
				for(GeocodingResult gr : result) {
					log.debug("gr : " + gr.formattedAddress + " ," + gr.geometry.location.lat + "," + gr.geometry.location.lng);
					if(calculCodePostal(gr)!=null) {
						listGrAvecCp.add(gr);
					}
				}
			}else if(result.length==1) {
				GeocodingResult gr = result[0];
				log.debug("gr : " + gr.formattedAddress + " ," + gr.geometry.location.lat + "," + gr.geometry.location.lng);
				listGrAvecCp.add(gr);
			}
			
			if(listGrAvecCp.size()==1){
			//if(listGrAvecCp.size()>0){
				GeocodingResult gr = listGrAvecCp.get(0);
				positionMdl.setLatitude(gr.geometry.location.lat);
				positionMdl.setLongitude(gr.geometry.location.lng);
				positionMdl.setTrouve(true);
				positionMdl.setAdresse(gr.formattedAddress);
				//if(listGrAvecCp.size()>1) {
				//	log.error("geocodeAdresse : plusieurs résultats pour adresse : " + adresse);
				//}
			}else if(listGrAvecCp.size()>1){
				positionMdl.setTrouve(false);
				positionMdl.setMultiple(true);
			}else{
				log.error("pb geocoding pour adresse : " + adresse);
				positionMdl.setTrouve(false);
			}
		} catch (Exception e) {
			log.error("geocodeAdresseStrict : " + e.toString() + " pour adresse : " + adresse);
			positionMdl.setTrouve(false);
		}
		
		return positionMdl;
	}
	
	public long calculDistance(String origin, String dest) {
		long distance = 0;
		try {
			if(origin!=null && dest!=null && isOriginDestinationOk(origin,dest)) {
				GeoApiContext context = new GeoApiContext.Builder()
					    .apiKey(Constantes.GOOGLE_GEOAPI_KEY)
					    .build();
				
				String[] origins = {origin};
				String[] destinations = {dest};
				
				DistanceMatrixApiRequest request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations);
				
				DistanceMatrix result = request.await();
				
				if(result!=null && result.rows!=null && result.rows.length>0) {
					DistanceMatrixRow distanceMatrixRow = result.rows[0];
					if(distanceMatrixRow.elements.length>0) {
						DistanceMatrixElement distanceMatrixElement = distanceMatrixRow.elements[0];
						Distance dis = distanceMatrixElement.distance;
						distance = dis.inMeters;
					}
				}
			}else {
				log.warn("calculDistance : orgin et/ou dest null : origin : " + origin + ", dest : " + dest);
			}
		}catch (Exception e) {
			log.error("calculDistance : " + e.toString() + " pour origin : " + origin + ", et dest : " + dest);
		}
		return distance;
	}
	
	boolean isOriginDestinationOk(String origin, String destination) {
		boolean isOriginDestinationOk = true;
		
		String[] partOrigin = origin.split(",");
		if(commonUtilService.parseDouble(partOrigin[0])==0 && commonUtilService.parseDouble(partOrigin[1])==0){
			isOriginDestinationOk = false;
		}
		if(isOriginDestinationOk) {
			String[] partDestination = destination.split(",");
			if(commonUtilService.parseDouble(partDestination[0])==0 && commonUtilService.parseDouble(partDestination[1])==0){
				isOriginDestinationOk = false;
			}
		}
		
		return isOriginDestinationOk;
	}
	
	private String calculCodePostal(GeocodingResult geocodingResult){
		String codePostal = null;
		
		AddressComponent[] tabac = geocodingResult.addressComponents;
		for(AddressComponent ac :tabac){
			if(Arrays.asList(ac.types).contains(AddressComponentType.POSTAL_CODE)){
				codePostal = ac.longName;
				break;
			}
		}
		return codePostal;
	}
}
