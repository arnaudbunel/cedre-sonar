package com.dnai.cedre.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("test")
@Slf4j
public class AmazonTestConfig {
    private static final String REGION = "eu-central-1";

    @Bean
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }
    
    @Lazy
    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }

    /*
    @Bean
    public DynamoDB dynamoDB() {
		AWSCredentials amazonAWSCredentials = new BasicAWSCredentials(
				"test1", "test231");

		String envDynamodbHost = System.getProperty("dynamodb.host");
		String endpoint = "http://localhost:8000";
		if(envDynamodbHost!=null && "jenkins".equals(envDynamodbHost)) {
			endpoint = "http://vps256110.ovh.net:8000";
		}
		
		AmazonDynamoDBAsync dyndbClient = AmazonDynamoDBAsyncClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials)).withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration(endpoint, "eu-central-1"))
				.build();
		DynamoDB dynamoDB = new DynamoDB(dyndbClient);
		log.info("init dynamoDB test avec endpoint : " + endpoint);         
        return dynamoDB;
    }*/
    
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
    	AmazonDynamoDB amazonDynamoDB = null;
    	try {
    		AWSCredentials amazonAWSCredentials = new BasicAWSCredentials(
    				"test1", "test231");

    		String envDynamodbHost = System.getProperty("dynamodb.host");
    		String endpoint = "http://localhost:8000";
    		if(envDynamodbHost!=null && "jenkins".equals(envDynamodbHost)) {
    			endpoint = "http://vps256110.ovh.net:8000";
    		}

    		log.info("init dynamoDB test avec endpoint : " + endpoint);
    		
    		amazonDynamoDB = AmazonDynamoDBClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials)).withEndpointConfiguration(
            				new AwsClientBuilder.EndpointConfiguration(endpoint, "eu-central-1"))
                    .build();
    	}catch(Exception e) {
    		log.error("amazonDynamoDB",e);
    	}
        return amazonDynamoDB;
    }
    
    @Bean
    public DynamoDB dynamoDB(final AmazonDynamoDB aDynamoDB) {
    	DynamoDB dynamoDB = new DynamoDB(aDynamoDB);
    	log.debug("dynamoDB : {}",(dynamoDB!=null?dynamoDB.toString():"null"));
        return dynamoDB;
    }
    
    @Bean
    @Autowired
    public DynamoDBMapper dynamoDBMapper(final AmazonDynamoDB aDynamoDB) {
        return new DynamoDBMapper(aDynamoDB);
    }
}