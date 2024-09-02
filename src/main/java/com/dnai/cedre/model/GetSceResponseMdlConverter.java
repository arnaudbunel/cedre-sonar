package com.dnai.cedre.model;

import java.io.StringWriter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.dnai.cedre.model.ose.GetSceResponseMdl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetSceResponseMdlConverter implements DynamoDBTypeConverter<String, GetSceResponseMdl>{

	@Override
	public String convert(GetSceResponseMdl object) {
        try {
			StringWriter xmlWriter = new StringWriter();
			Serializer serializer = new Persister();
			serializer.write(object, xmlWriter);
            return xmlWriter.toString();
        } catch (Exception ex) {
            log.error("Error while serializing object", ex);
        }
        return null;
	}

	@Override
	public GetSceResponseMdl unconvert(String object) {
        try {
			Serializer serializer = new Persister();
			return serializer.read(GetSceResponseMdl.class, object, false);
        } catch (Exception ex) {
            log.error("Error while reading object '{}'", object, ex);
        }
        return null;
	}
}
