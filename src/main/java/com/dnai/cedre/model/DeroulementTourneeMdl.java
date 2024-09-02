package com.dnai.cedre.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.dnai.cedre.util.Constantes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@DynamoDBTable(tableName = Constantes.AWS_DYNDB_TABLE_CEDRE_DEROULEMENT)
@NoArgsConstructor
@AllArgsConstructor
public class DeroulementTourneeMdl {

    @DynamoDBHashKey(attributeName = Constantes.AWS_DYNDB_CHAMP_TOKEN)
    private String token;

    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_CONTEXTE)
    private String contexte;
    
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_EXPIRATION_STOCKAGE)
    private long expirationStockage;
	
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_LASTMAJ)
    private String lastmaj;
    
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_TOURNEEID)
    private String tourneeId;
    
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_VERSION)
    private String version;
    
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_CODE_SERVICE)
    private String codeservice;
    
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_TOURNEE)
    @DynamoDBTypeConverted(converter = TourneeInfoMdlConverter.class)
    private TourneeInfoMdl tourneeMdl;
    
    @DynamoDBAttribute(attributeName = Constantes.AWS_DYNDB_CHAMP_SERVICE_OSE)
    private String cleServiceOse;
}
