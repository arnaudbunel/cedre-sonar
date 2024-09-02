package com.dnai.cedre.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
@Profile("!test")
public class AmazonConfig {
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

    @Lazy
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(REGION)
                .build();
    }

    @Bean
    @Autowired
    public DynamoDB dynamoDB(final AmazonDynamoDB aDynamoDB) {
        return new DynamoDB(aDynamoDB);
    }

    @Bean
    @Autowired
    public DynamoDBMapper dynamoDBMapper(final AmazonDynamoDB aDynamoDB) {
        return new DynamoDBMapper(aDynamoDB);
    }
}