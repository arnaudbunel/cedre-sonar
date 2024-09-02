package com.dnai.cedre.util;

public class Constantes {

	// TODO à regénérer ?
	public static final String TOKEN_KEY = "9E@_mM)QEY[;s{^W@s+?";
	public static final String TOKEN_ISSUER = "cedre.adn.ai";
	public static final int TOKEN_DUREE_VIE = 600; // en minutes
	public static final String TOKEN_KEY_IDENTIFIANT = "identifiant";
	public static final String TOKEN_HTTP_HEADER_KEY = "Authorization";
	public static final String TOKEN_HTTP_HEADER_BEARER_KEY = "Bearer";
	public static final String HEADER_ADNAI_VERSION = "Adnai-Version";
	public static final String HEADER_ADNAI_TOKEN = "Adnai-Token";
	
	public static final String PATTERN_HHMMSS = "HH:mm:ss";
	public static final String PATTERN_HHMM = "HH:mm";
	public static final String PATTERN_DATESLASH = "yyyy/MM/dd";
	public static final String PATTERN_DATETIRET = "yyyy-MM-dd";
	public static final String PATTERN_DATEHEURE = "yyyy-MM-dd HH:mm";
	
	public static final String DRL_CTX_CHOIX_TOURNEE = "CHOIX_TOURNEE";
	public static final String DRL_CTX_DEBUT_CLIENT = "DEBUT_CLIENT";
	public static final String DRL_CTX_FIN_CLIENT = "FIN_CLIENT";
	public static final String DRL_CTX_FIN_TOURNEE = "FIN_TOURNEE";
	public static final String DRL_CTX_DEBUT_TOURNEE = "DEBUT_TOURNEE";
	
	public static final String PREFIX_TOURNEE_ADNAITEST = "adnaitest";
	
	public static final String OSE_SERVICE_GETSCE = "ose.getsce";
	public static final String OSE_SERVICE_PUT = "ose.put";
	public static final String OSE_SERVICE_COCKPIT_LOGIN = "ose.cockpit.login";
	public static final String OSE_SERVICE_GETSIGNALEMENTS = "ose.getsignalements";
	
	public static final String HTTP_HEADER_CONTENTTYPE = "Content-type";
	public static final String HTTP_HEADER_ACCEPT = "Accept";
	public static final String HTTP_HEADER_CONTENTTYPE_VALUE_XML = "text/xml; charset=utf-8";

	public static final String AWS_DYNDB_TABLE_CEDRE_DEROULEMENT = "cedre_deroulement";
	
	public static final String AWS_DYNDB_CHAMP_TOKEN = "token";
	public static final String AWS_DYNDB_CHAMP_CONTEXTE = "contexte";
	public static final String AWS_DYNDB_CHAMP_VERSION = "version";
	public static final String AWS_DYNDB_CHAMP_LASTMAJ = "lastmaj";
	public static final String AWS_DYNDB_CHAMP_TOURNEEID = "tourneeid";
	public static final String AWS_DYNDB_CHAMP_TOURNEE = "tournee";
	public static final String AWS_DYNDB_CHAMP_EXPIRATION_STOCKAGE = "expirationStockage";
	public static final String AWS_DYNDB_CHAMP_SERVICE_OSE = "serviceOse";
	public static final String AWS_DYNDB_CHAMP_CODE_SERVICE = "codeservice";
	public static final String AWS_DYNDB_CHAMP_ERREUR = "erreur";
	public static final String AWS_DYNDB_CHAMP_DEBUG = "debug";
	
	public static final String GOOGLE_GEOAPI_KEY = "AIzaSyBonKz27EzLiMHQoZrPzEbtX6P8ON_k-e0"; // projet infra

	public static final String S3_BUCKET = "cedre.adn.ai";
	public static final String S3_AUDIO_PREFIX = "audio";
	public static final String S3_AUDIO_FOLDER = "audio/";
	public static final String S3_REGION = "eu-central-1";
	public static final String S3_AUDIO_URL = "https://s3.eu-central-1.amazonaws.com/cedre.adn.ai/audio/";
	public static final String S3_USRMTDT_IDTOURNEE = "idtournee";
	public static final String S3_USRMTDT_IDCLIENT = "idclient";
	public static final String AUDIO_WAV_EXT = ".wav";
	public static final String AUDIO_WAV_CONTENT_TYPE = "audio/wav";
	public static final String IMAGE_PNG_EXT = ".png";
	public static final String S3_IMAGE_PREFIX = "image";
	public static final String S3_IMAGE_FOLDER = "image/";
	public static final String IMAGE_PNG_CONTENT_TYPE = "image/png";
	public static final String S3_IMAGE_URL = "https://s3.eu-central-1.amazonaws.com/cedre.adn.ai/image/";
	
	public static final String AVP_EXT = ".pdf";
	public static final String S3_AVP_PREFIX = "avisdepassage";
	public static final String S3_AVP_FOLDER = "avisdepassage/";
	public static final String AVP_CONTENT_TYPE = "application/pdf";
	public static final String S3_AVP_URL = "https://s3.eu-central-1.amazonaws.com/cedre.adn.ai/avisdepassage/";

	public static final String S3_MOCKDATA_URL = "https://s3.eu-central-1.amazonaws.com/cedre.adn.ai/mockdata/";
	
	public static final String FLUXOSE_EXT = ".xml";
	public static final String FLUXOSE_CONTENT_TYPE = "text/xml";
	public static final String S3_FLUXOSE_FOLDER = "fluxose/";
		
	public static final String FLUXSVCINFO_EXT = ".json";
	public static final String FLUXSVCINFO_CONTENT_TYPE = "application/json";
	public static final String S3_FLUXSVCINFO_FOLDER = "svcinfo/";
	public static final String S3_FLUXSVCINFO_URL = "https://s3.eu-central-1.amazonaws.com/cedre.adn.ai/svcinfo/";
	
	public static final String ALLOW_ORIGIN = "allow.origin";
	public static final String DIRECTORY_SIGNATURE = "directory.signature";
	
	public static final String PRESTATION_COLLECTE_MIN = "collecte";
	public static final String PRESTATION_COLLECTE = "Collecte";
	public static final String PRESTATION_COLLECTE_LEMON = "Collecte Lemon";
	public static final String PRESTATION_DEPOTRETRAIT = "Dépôt/Retrait";
	public static final String PRESTATION_DEPOT = "Dépôt";
	public static final String PRESTATION_RETRAIT = "Retrait";
	public static final String PRESTATION_FOURNITURE = "Fourniture";
	public static final String PRESTATION_MAIN_DOEUVRE = "Main d'oeuvre";
	public static final String PRESTATION_VIDAGE = "Vidage";
	public static final String PRESTATION_RETRAIT_DEFINITIF = "Retrait définitif";
	public static final String PRESTATION_EMPORT = "Emport";
	public static final String PRESTATION_TRANSPORT = "Transport";
	
	
	public static final String DISPO_CORBEILLE = "corbeille";
	public static final String DISPO_BONBONNE = "bonbonne";
	public static final String DISPO_SAC = "Sacs";
	public static final String DISPO_ALU = "ALU";
	
	public static final String TOURNEE_ETAT_DEBUT = "DEBUT";
	public static final String TOURNEE_ETAT_ENCOURS = "ENCOURS";
	public static final String TOURNEE_ETAT_FIN = "FIN";
	
	public static final String COLLECTE_ETAT_ATTENTE = "ATTENTE";
	public static final String COLLECTE_ETAT_DEBUT = "DEBUT";
	public static final String COLLECTE_ETAT_IMPOSSIBLE = "IMPOSSIBLE";
	public static final String COLLECTE_ETAT_TRAITE = "TRAITE";
	
	public static final String TEMPS_REEL_ETAT_AFAIRE = "AFAIRE";
	public static final String TEMPS_REEL_ETAT_SURPLACE = "SURPLACE";
	public static final String TEMPS_REEL_ETAT_ENCOURS = "ENCOURS";
	public static final String TEMPS_REEL_ETAT_TRAITE = "TRAITE";
	public static final String TEMPS_REEL_ETAT_IMPOSSIBLE = "IMPOSSIBLE";
	
	public static final String TEMPS_REEL_ETAT_TOURNEE_VERS_CLIENT = "ETAT_TOURNEE_VERS_CLIENT"; // Gris : Camion roule au prochain client
	public static final String TEMPS_REEL_ETAT_TOURNEE_CLIENT_ENCOURS = "ETAT_TOURNEE_CLIENT_ENCOURS"; // Orange : Prestation en cours
	public static final String TEMPS_REEL_ETAT_TOURNEE_CLIENT_PROXIMITE = "ETAT_TOURNEE_CLIENT_PROXIMITE"; // Jaune : Proximité client
	public static final String TEMPS_REEL_ETAT_TOURNEE_VERS_CENTRE = "ETAT_TOURNEE_VERS_CENTRE"; // Bleu :Dernier client terminé, camion sur le retour
	public static final String TEMPS_REEL_ETAT_TOURNEE_PROX_CENTRE = "ETAT_TOURNEE_PROX_CENTRE"; // Violet : Retour proximité centre
	public static final String TEMPS_REEL_ETAT_TOURNEE_TRAITE_TOTAL = "ETAT_TOURNEE_TRAITE_TOTAL"; // Vert : Fin de tournée tous les clients ont été fait
	public static final String TEMPS_REEL_ETAT_TOURNEE_TRAITE_PARTIEL = "ETAT_TOURNEE_TRAITE_PARTIEL"; // Orange : Fin de tournée, x client non fait.
	
	// DIB
	public static final String TEMPS_REEL_ETAT_TOURNEE_VERS_VIDAGE = "ETAT_TOURNEE_VIDAGE"; // Violet : vers point de vidage 
	
	
	public static final int CENTRE_TEST = 99;
	
	public final static String EVENT_TOURNEE_MESSAGE = "MESSAGE";
	public final static String EVENT_TOURNEE_FUSION = "FUSION";
	public final static String EVENT_TOURNEE_UPDATE = "UPDATE";
	
	public final static String SQS_QUEUE_EVENT_COLLECTE = "sqs.queue.event.collecte";
	
	public final static String SQS_QUEUE_MAJTOURNEEOSE = "sqs.queue.majtourneeose";
	
	public static final String CREATION_COMPTE_SUBJECT = "Création de votre compte sur Cockpit";
	public static final String CODE_CONF_CREATION_COMPTE = "cedre-creationcompte";

	public static final String IDSIGNALEMENT_AUTRE = "54";
}
