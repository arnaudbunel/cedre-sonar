package com.dnai.cedre.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dnai.cedre.util.ApplicationContextHolder;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TourneeInfoMdlConverter implements DynamoDBTypeConverter<String, TourneeInfoMdl>{

    @Setter
    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if(this.objectMapper == null) {
            this.objectMapper = ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);
        }

        return this.objectMapper;
    }
    
	@Override
	public String convert(TourneeInfoMdl object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (Exception ex) {
            log.error("Error while serializing object", ex);
        }
        return null;
	}

	@Override
	public TourneeInfoMdl unconvert(String object) {
        try {
            return getObjectMapper().readValue(object, TourneeInfoMdl.class);
        } catch (Exception ex) {
            log.error("Error while reading object '{}'", object, ex);
        }
        return null;
	}
}
